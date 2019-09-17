package ru.iqsolution.tkoonline.data.local

import android.util.Base64
import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCBCPKCS7 {

    private val IV_BYTES = byteArrayOf(
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
    )

    private val KEY_BYTES = byteArrayOf(
        *IV_BYTES, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f
    )

    fun encrypt(data: Any): String? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(KEY_BYTES, "AES"),
                IvParameterSpec(IV_BYTES)
            )
            Base64.encode(cipher.doFinal(data.toString().toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
                .toString(Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun decrypt(data: String): String? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(KEY_BYTES, "AES"),
                IvParameterSpec(IV_BYTES)
            )
            return cipher.doFinal(Base64.decode(data, Base64.NO_WRAP))
                .toString(Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }
}