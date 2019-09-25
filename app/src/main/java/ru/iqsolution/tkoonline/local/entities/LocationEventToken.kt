package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class LocationEventToken {

    @Embedded
    lateinit var location: LocationEvent

    @Embedded
    lateinit var token: AccessToken
}