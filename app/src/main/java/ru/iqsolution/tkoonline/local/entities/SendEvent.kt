package ru.iqsolution.tkoonline.local.entities

interface SendEvent {

    var id: Long?

    var tokenId: Long

    var sent: Boolean
}