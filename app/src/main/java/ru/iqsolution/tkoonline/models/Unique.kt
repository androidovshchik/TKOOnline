package ru.iqsolution.tkoonline.models

import java.time.LocalDate

interface Unique {

    var tokenId: Long

    var routeId: String?

    var whenDay: LocalDate
}