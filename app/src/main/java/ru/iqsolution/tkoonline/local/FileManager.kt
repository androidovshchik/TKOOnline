package ru.iqsolution.tkoonline.local

import android.content.Context
import android.graphics.*
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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
    fun copyFile(src: File?, dist: File?) {
        if (src == null || dist == null) {
            return
        }
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
                file.readBitmap()
                    ?.compress(Bitmap.CompressFormat.JPEG, 80, output)

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
            MultipartBody.Part.createFormData(
                "file", file.name,
                file.asRequestBody(IMAGE_TYPE)
            )
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
        // First decode with inJustDecodeBounds=true to check dimensions
        var scaledBitmap: Bitmap? = null
        var bmp: Bitmap? = null

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        bmp = BitmapFactory.decodeFile(absolutePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = reqWidth / reqHeight

        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            //If Height is greater
            if (imgRatio < maxRatio) {
                imgRatio = reqHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = reqHeight.toInt()

            }  //If Width is greater
            else if (imgRatio > maxRatio) {
                imgRatio = reqWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = reqWidth.toInt()
            } else {
                actualHeight = reqHeight.toInt()
                actualWidth = reqWidth.toInt()
            }
        }
        // Calculate inSampleSize
        options.inSampleSize = options.calculateInSampleSize(actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            bmp = BitmapFactory.decodeFile(absolutePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp, middleX - bmp!!.getWidth() / 2,
            middleY - bmp!!.getHeight() / 2, Paint(Paint.FILTER_BITMAP_FLAG)
        )
        bmp!!.recycle()
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90)
                3 -> matrix.postRotate(180)
                8 -> matrix.postRotate(270)
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0, scaledBitmap!!.getWidth(),
                scaledBitmap!!.getHeight(), matrix, true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return scaledBitmap
    }

    private fun BitmapFactory.Options.calculateInSampleSize(reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        var inSampleSize = 1
        if (outHeight > reqHeight || outWidth > reqWidth) {
            inSampleSize *= 2
            val halfHeight = outHeight / 2
            val halfWidth = outWidth / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {

        private const val LIFETIME = 4 * 24 * 60 * 60 * 1000L

        private val IMAGE_TYPE = "image/jpeg".toMediaTypeOrNull()
    }
}
