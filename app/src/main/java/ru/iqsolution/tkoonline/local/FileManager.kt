package ru.iqsolution.tkoonline.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.joda.time.DateTime
import ru.iqsolution.tkoonline.DB_NAME
import ru.iqsolution.tkoonline.extensions.PATTERN_TIME_MILLIS
import ru.iqsolution.tkoonline.extensions.use
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class FileManager(context: Context) {

    val externalDir: File? = context.getExternalFilesDir(null)?.apply { mkdirs() }

    val internalDir: File = context.filesDir

    val photosDir: File
        get() = File(internalDir, "photos").apply { mkdirs() }

    val backupDir: File?
        get() = externalDir?.let {
            File(it, "backup").apply { mkdirs() }
        }

    val logsDir: File
        get() = File(externalDir ?: internalDir, "logs").apply { mkdirs() }

    val dbFile: File = context.getDatabasePath(DB_NAME)

    val configFile: File
        get() = File(externalDir ?: internalDir, "config.json")

    val apkFile: File?
        get() = externalDir?.let {
            File(it, "app.apk")
        }

    val randomName: String
        get() = "${UUID.randomUUID()}.jpg"

    fun copyDb(): Boolean {
        backupDir?.let {
            val datetime = DateTime.now().toString(PATTERN_TIME_MILLIS)
            val distFile = File(it, "app_${datetime}.db")
            return writeFile(distFile) { output ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(output)
                }
            }
        }
        return false
    }

    fun copyImage(src: String, dist: String) {
        copyImage(File(src), File(dist))
    }

    /**
     * @return new path of file
     */
    @WorkerThread
    fun copyImage(src: File, dist: File) {
        if (!src.exists()) {
            return
        }
        readBitmap(src)?.use {
            writeFile(dist) {
                compress(Bitmap.CompressFormat.JPEG, 75, it)
            }
        }
    }

    fun readBitmap(path: String): Bitmap? {
        return readBitmap(File(path))
    }

    fun readBitmap(file: File): Bitmap? {
        if (!file.exists()) {
            return null
        }
        try {
            val bitmap = BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.path, this)
                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(MAX_SIZE, MAX_SIZE)
                inJustDecodeBounds = false
                // Decode bitmap with inSampleSize set
                BitmapFactory.decodeFile(file.path, this)
            }
            val matrix = Matrix()
            val width = bitmap.width
            val height = bitmap.height
            if (width > MAX_SIZE || height > MAX_SIZE) {
                val ratio = width.toFloat() / height
                val newWidth = if (ratio < 1) MAX_SIZE * ratio else MAX_SIZE.toFloat()
                val scale = newWidth / width
                matrix.preScale(scale, scale)
            }
            val exif = ExifInterface(file)
            val rotation = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
            if (rotation % 360 != 0) {
                matrix.postRotate(rotation.toFloat())
            }
            val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            if (newBitmap != bitmap) {
                try {
                    bitmap.recycle()
                } catch (e: Throwable) {
                    Timber.e(e)
                }
            }
            return newBitmap
        } catch (e: Throwable) {
            Timber.e(e)
            return null
        }
    }

    fun readFile(path: String): MultipartBody.Part? {
        return readFile(File(path))
    }

    @WorkerThread
    fun readFile(file: File): MultipartBody.Part? {
        if (!file.exists()) {
            return null
        }
        return try {
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(IMAGE_TYPE))
        } catch (e: Throwable) {
            Timber.e(e)
            null
        }
    }

    fun deleteFile(path: String) {
        deleteFile(File(path))
    }

    fun deleteFile(file: File?) {
        try {
            if (file?.isFile == true) {
                file.delete()
            }
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    @WorkerThread
    fun deleteOldFiles() {
        val now = System.currentTimeMillis()
        externalDir?.deleteFiles { it.extension != "json" }
        photosDir.deleteFiles { now - it.lastModified() >= FILE_LIFETIME }
        backupDir?.deleteFiles { now - it.lastModified() >= FILE_LIFETIME }
        logsDir.deleteFiles { now - it.lastModified() >= FILE_LIFETIME }
    }

    /**
     * For debug purposes only
     */
    @WorkerThread
    fun deleteAllFiles() {
        externalDir?.deleteFiles { true }
        photosDir.deleteFiles { true }
        backupDir?.deleteFiles { true }
        logsDir.deleteFiles { true }
    }

    private inline fun File.deleteFiles(predicate: (File) -> Boolean) {
        listFiles()?.forEach {
            if (it.isFile && predicate(it)) {
                deleteFile(it)
            }
        }
    }

    @WorkerThread
    inline fun writeFile(dist: File, block: (FileOutputStream) -> Unit): Boolean {
        return try {
            FileOutputStream(dist).use { output ->
                block(output)
                output.flush()
            }
            true
        } catch (e: Throwable) {
            Timber.e(e)
            deleteFile(dist)
            false
        }
    }

    private fun BitmapFactory.Options.calculateInSampleSize(reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = outHeight to outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {

        private const val MAX_SIZE = 1600

        private const val FILE_LIFETIME = 4 * 24 * 60 * 60 * 1000L

        private val IMAGE_TYPE = "image/jpeg".toMediaTypeOrNull()
    }
}
