package ru.iqsolution.tkoonline.remote.api

import com.google.gson.annotations.SerializedName
import ru.iqsolution.tkoonline.local.entities.*

abstract class ResponseData<T> {

    @SerializedName("data")
    lateinit var data: List<T>
}

class ResponseRoutes : ResponseData<Route>()

class ResponseTasks : ResponseData<Task>()

class ResponsePhones : ResponseData<Contact>()

class ResponseTaskTypes : ResponseData<TaskType>()

class ResponsePhotoTypes : ResponseData<PhotoType>()

class ResponseTask : ResponseData<TaskEvent>() {

    @SerializedName("id")
    var id = 0
}

class ResponsePhoto : ResponseData<PhotoEvent>() {

    @SerializedName("id")
    var id = 0
}