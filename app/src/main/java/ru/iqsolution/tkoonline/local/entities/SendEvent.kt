package ru.iqsolution.tkoonline.local.entities

import org.joda.time.DateTime

interface SendEvent {

    var id: Long?

    var tokenId: Long

    /**
     * [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    var whenTime: DateTime

    var sent: Boolean
}