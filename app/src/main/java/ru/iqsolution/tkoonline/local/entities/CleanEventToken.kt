package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class CleanEventToken {

    @Embedded
    lateinit var clean: CleanEvent

    @Embedded
    lateinit var token: AccessToken
}