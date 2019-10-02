package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.local.entities.PhotoEvent

interface GalleryListener {

    fun onPhotoClick(photoEvent: PhotoEvent?)
}