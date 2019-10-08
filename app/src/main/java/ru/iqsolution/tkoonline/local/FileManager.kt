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
import ru.iqsolution.tkoonline.extensions.use
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class FileManager(context: Context) {

    val externalDir = context.getExternalFilesDir(null)?.apply {
        mkdirs()
    }

    val internalDir: File = context.filesDir

    val photosDir = File(internalDir, "photos").apply {
        mkdirs()
    }

    fun getRandomName(): String {
        return "${UUID.randomUUID()}.jpg"
    }

    fun copyFile(src: String, dist: String) {
        copyFile(File(src), File(dist))
    }

    /**
     * @return new path of file
     */
    @WorkerThread
    fun copyFile(src: File, dist: File) {
        if (!src.exists()) {
            return
        }
        try {
            FileInputStream(src).use { input ->
                FileOutputStream(dist).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
            dist.delete()
        }
    }

    fun compressImage(path: String) {
        compressImage(File(path))
    }

    fun compressImage(file: File) {
        if (!file.exists()) {
            return
        }
        try {
            FileOutputStream(file).use { output ->
                file.readBitmap()?.use {
                    compress(Bitmap.CompressFormat.JPEG, 75, output)
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
            file.delete()
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
            file?.delete()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    @WorkerThread
    fun deleteOldFiles() {
        val now = System.currentTimeMillis()
        photosDir.listFiles().forEach {
            if (now - it.lastModified() >= LIFETIME) {
                deleteFile(it)
            }
        }
    }

    /**
     * For debug purposes only
     */
    @WorkerThread
    fun deleteAllFiles() {
        photosDir.listFiles().forEach {
            deleteFile(it)
        }
    }

    @Suppress("DEPRECATION")
    private fun File.readBitmap(): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(path)
        try {
            val exif = ExifInterface(this)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return bitmap
    }

    companion object {

        private const val LIFETIME = 4 * 24 * 60 * 60 * 1000L

        private val IMAGE_TYPE = "image/jpeg".toMediaTypeOrNull()
    }
}
