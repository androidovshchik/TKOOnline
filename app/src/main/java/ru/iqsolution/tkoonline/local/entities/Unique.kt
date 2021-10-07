package ru.iqsolution.tkoonline.local.entities

import java.time.LocalDate

interface Unique {

    var tokenId: Long

    var routeId: String?

    var whenDay: LocalDate
}