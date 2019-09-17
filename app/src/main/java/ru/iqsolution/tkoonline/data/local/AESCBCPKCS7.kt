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
        0x36,
        0x44,
        0x36,
        0x31,
        0x32,
        0x41,
        0x38,
        0x36,
        0x42,
        0x45,
        0x45,
        0x34,
        0x42,
        0x30,
        0x41,
        0x36,
        0x35,
        0x39,
        0x42,
        0x38,
        0x42,
        0x33,
        0x41,
        0x46,
        0x46,
        0x44,
        0x36,
        0x46,
        0x31,
        0x46,
        0x42,
        0x43,
        0x41,
        0x44,
        0x31,
        0x35,
        0x43,
        0x34,
        0x43,
        0x42,
        0x42,
        0x42,
        0x42,
        0x44,
        0x43,
        0x34,
        0x39,
        0x39,
        0x36,
        0x43,
        0x36,
        0x43,
        0x30,
        0x31,
        0x43,
        0x37,
        0x38,
        0x36,
        0x37,
        0x31,
        0x31,
        0x41,
        0x32,
        0x31
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