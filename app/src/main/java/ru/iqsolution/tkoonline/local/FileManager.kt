package ru.iqsolution.tkoonline.local

import android.content.Context
import androidx.annotation.WorkerThread
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("MemberVisibilityCanBePrivate")
class FileManager(context: Context) {

    val externalDir = context.getExternalFilesDir(null)?.apply {
        mkdirs()
    }

    val internalDir: File = context.filesDir

    val photosDir = File(internalDir, "photos").apply {
        mkdirs()
    }

    fun createTempFile(): File {
        return File.createTempFile("photo", ".jpeg", externalDir)
    }

    fun moveToInternal(path: String): String {
        return moveToInternal(File(path))
    }

    /**
     * @return new path of file
     */
    @WorkerThread
    fun moveToInternal(file: File): String {
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

    @WorkerThread
    fun readFile(file: File): MultipartBody.Part? {
        try {
            if (file.exists()) {
                return MultipartBody.Part.createFormData(
                    "file", file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    fun deleteFile(path: String) {
        deleteFile(File(path))
    }

    fun deleteFile(file: File) {
        try {
            file.delete()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    @WorkerThread
    fun deleteOldFiles() {
        val now = System.currentTimeMillis()
        photosDir.listFiles().forEach {
            try {
                if (now - it.lastModified() >= LIFETIME) {
                    it.delete()
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    companion object {

        private const val LIFETIME = 4 * 24 * 60 * 60 * 1000L
    }
}
