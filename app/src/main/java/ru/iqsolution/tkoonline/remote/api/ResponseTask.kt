package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.TaskEvent

class ResponseTask {

    @SerializedName("id")
    var id = 0

    @SerializedName("task_id")
    var taskId = 0

    @SerializedName("data")
    lateinit var event: TaskEvent
}