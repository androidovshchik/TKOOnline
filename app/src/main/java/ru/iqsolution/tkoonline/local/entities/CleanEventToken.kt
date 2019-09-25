package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class CleanEventToken {

    @Embedded
    lateinit var cleaning: CleanEvent

    @Embedded
    lateinit var token: AccessToken
}