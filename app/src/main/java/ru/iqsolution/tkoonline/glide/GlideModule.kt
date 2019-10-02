package ru.iqsolution.tkoonline.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import java.io.File

@GlideModule
class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.apply {
            append(PhotoEvent::class.java, File::class.java, PhotoLoader.Factory())
        }
    }
}