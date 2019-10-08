package ru.iqsolution.tkoonline.local

import android.content.Context
import android.graphics.*
import androidx.annotation.WorkerThread
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import android.graphics.Bitmap.CompressFormat
import androidx.exifinterface.media.ExifInterface
import java.io.IOException


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

    fun compressImage(
        imageFile: File,
        reqWidth: Float,
        reqHeight: Float,
        compressFormat: Bitmap.CompressFormat,
        quality: Int,
        destinationPath: String
    ): File {
        var fileOutputStream: FileOutputStream? = null
        val file = File(destinationPath).parentFile
        if (!file.exists()) {
            file.mkdirs()
        }
        try {
            fileOutputStream = FileOutputStream(destinationPath)
            // write the compressed bitmap at the destination specified by destinationPath.
            decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(
                compressFormat,
                quality,
                fileOutputStream
            )
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }

        return File(destinationPath)
    }

    fun readFile(path: String): MultipartBody.Part? {
        return readFile(File(path))
    }

    @WorkerThread
    fun readFile(file: File): MultipartBody.Part? {
        try {
            if (file.exists()) {
                return MultipartBody.Part.createFormData(
                    "file", file.name,
                    file.asRequestBody(IMAGE_TYPE)
                )
            }
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return null
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

    private fun File.decodeSampledBitmapFromFile(reqWidth: Float, reqHeight: Float): Bitmap? {
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
            if (orientation == 6) {
                matrix.postRotate(90)
            } else if (orientation == 3) {
                matrix.postRotate(180)
            } else if (orientation == 8) {
                matrix.postRotate(270)
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
