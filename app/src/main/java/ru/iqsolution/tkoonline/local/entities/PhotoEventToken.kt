package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class PhotoEventToken {

    @Embedded
    lateinit var photo: PhotoEvent

    @Embedded
    lateinit var token: AccessToken
}