package ru.iqsolution.tkoonline.local.entities

import androidx.room.Embedded

class TaskEventToken {

    @Embedded
    lateinit var task: TaskEvent

    @Embedded
    lateinit var token: Token
}