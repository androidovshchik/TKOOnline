package ru.iqsolution.tkoonline.screens.platform

import android.content.Context
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.screens.base.user.UserPresenter
import timber.log.Timber
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

class PlatformPresenter(context: Context) : UserPresenter<PlatformContract.View>(context),
    PlatformContract.Presenter {

    override fun generateSignature(lat: Double, lon: Double) {
        launch {
            reference.get()?.signature = withContext(Dispatchers.Default) {
                val uri = Uri.Builder()
                    .scheme("yandexnavi")
                    .authority("build_route_on_map")
                    .appendQueryParameter("lat_to", lat.toString())
                    .appendQueryParameter("lon_to", lon.toString())
                    .appendQueryParameter("client", "270")
                    .build()
                try {
                    val key = Base64.decode(yandexKey, Base64.DEFAULT)
                    val signature = Signature.getInstance("SHA256withRSA")
                    signature.initSign(
                        KeyFactory.getInstance("RSA")
                            .generatePrivate(PKCS8EncodedKeySpec(key))
                    )
                    signature.update(uri.toString().toByteArray(Charsets.UTF_8))
                    val sign = Base64.encodeToString(signature.sign(), Base64.NO_WRAP)
                    uri.buildUpon()
                        .appendQueryParameter("signature", sign)
                        .build()
                } catch (e: Throwable) {
                    Timber.e(e)
                    uri
                }
            }
        }
    }

    override fun loadLinkedPlatforms(linkedIds: List<Int>) {
        launch {
            val linkedPlatforms = withContext(Dispatchers.IO) {
                db.platformDao().getFromIds(linkedIds)
            }
            reference.get()?.onLinkedPlatforms(linkedPlatforms)
        }
    }

    override fun loadCleanEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val cleanEvents = withContext(Dispatchers.IO) {
                db.cleanDao().getDayKpEvents(day, kpId)
            }
            if (cleanEvents != null) {
                reference.get()?.onCleanEvents(cleanEvents)
            }
        }
    }

    override fun loadPhotoEvents(kpId: Int) {
        val day = preferences.serverDay
        launch {
            val photoEvents = withContext(Dispatchers.IO) {
                db.photoDao().getDayKpEvents(day, kpId)
            }
            reference.get()?.onPhotoEvents(photoEvents)
        }
    }

    override fun savePlatformEvents(
        platform: PlatformContainers,
        platforms: List<Platform>,
        clear: Boolean
    ) {
        val day = preferences.serverDay
        val tokenId = preferences.tokenId
        val cleanEvent = CleanEvent(platform.kpId, tokenId).apply {
            setFromAny(platform)
        }
        val cleanEvents = platforms.map {
            CleanEvent(it.kpId, tokenId).apply {
                linkedId = it.linkedKpId
                setFromAny(it)
            }
        }
        launch {
            withContext(Dispatchers.IO) {
                val validKpIds = db.cleanDao().insertMultiple(cleanEvent, cleanEvents)
                db.photoDao().markReadyMultiple(day, platform.allKpIds, validKpIds)
                db.platformDao().updateStatus(
                    validKpIds,
                    if (clear) PlatformStatus.CLEANED.id else PlatformStatus.NOT_CLEANED.id
                )
            }
            reference.get()?.closeDetails(true)
        }
    }

    override fun detachView() {
        // no cancelling
        reference.clear()
    }

    companion object {

        private val yandexKey = """
            MIIBOwIBAAJBANmqg5DbinsYn6UofV7gGvl4bCyGH6FkwIyWJgSLze3SIcSM8gLq
            oWBpkinhKExaToT0rpV/4RJOGiV7V7VKX8MCAwEAAQJBALHPkpSmU7UFQ30m22vC
            xSZgUnX4xyQP6x+tlLcIAhywlu4gCvsu7uDXAfssmHN5nl3tXA7JuCZHL0oF7oR3
            bdkCIQD2g2QxfoMy9crc3dw4+ncodGNg8d+f6Re2+GSbbSIPpQIhAOIK7MFJNhaX
            YxwEdTFYhiY+ao1Yd7mNBSWpr3/6OZVHAiAWoAKPJFxoTfTbhqVSuXI8TUpduHVc
            2OjrSyr4tPB+XQIhAKZMMUg2K2PNdm3LXciy/uat7sgUOOi6tfmyb9owZiLLAiAC
            OVa+FvQrZTTNXRG2wVW/pXBIubdmCaIDI4tSOcubng==
        """.replace("\\s".toRegex(), "")
    }
}