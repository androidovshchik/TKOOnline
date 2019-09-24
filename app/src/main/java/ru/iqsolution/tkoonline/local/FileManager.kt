package ru.iqsolution.tkoonline.local

import android.content.Context
import android.os.Environment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("MemberVisibilityCanBePrivate")
class FileManager(context: Context) {

    private val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.apply {
        mkdirs()
    }

    private val filesDir = context.filesDir

    private val photosDir = File(filesDir, "photos").apply {
        mkdirs()
    }

    fun createFile(): File {
        return File.createTempFile("photo", ".jpeg", picturesDir)
    }

    fun moveFile(path: String): String {
        return moveFile(File(path))
    }

    /**
     * @return new path of file
     */
    fun moveFile(file: File): String {
        val dist = File(photosDir, file.name)
        try {
            FileInputStream(file).use { input ->
                FileOutputStream(dist).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            dist.delete()
        }
        return dist.path
    }

    fun readFile(path: String): MultipartBody.Part? {
        return readFile(File(path))
    }

    fun readFile(file: File): MultipartBody.Part? {
        return if (file.exists()) {
            MultipartBody.Part.createFormData(
                "file", file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
        } else null
    }

    fun deleteFile(path: String) {
        File(path).delete()
    }
}
