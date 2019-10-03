package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType

interface GalleryListener {

    fun onPhotoClick(photoType: PhotoType.Default, photoEvent: PhotoEvent?)
}