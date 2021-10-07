@file:Suppress("unused")

package ru.iqsolution.tkoonline.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.http.*
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.local.entities.TaskEvent
import ru.iqsolution.tkoonline.remote.api.*

interface Server {

    @Tag("login")
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("$API_VERSION/auth")
    suspend fun login(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("block_code") blockCode: Int?,
        @Field("phone_number") phone: String,
        @Field("version") version: String = BuildConfig.VERSION_NAME
    ): ResponseAuth

    /**
     * @param date [patternDate]
     */
    @Tag("routes")
    @Headers("Accept: application/json")
    @GET("$API_VERSION/routes/{date}")
    suspend fun getRoutes(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): ResponseRoutes

    /**
     * @param date [patternDate]
     */
    @Tag("tasks")
    @Headers("Accept: application/json")
    @GET("$API_VERSION/routes/{date}/tasks")
    suspend fun getTasks(
        @Header("Authorization") token: String,
        @Path("date") date: String,
        @Query("route_number") route: String
    ): ResponseTasks

    @Tag("phones")
    @Headers("Accept: application/json")
    @GET("$API_VERSION/dispatchers")
    suspend fun getPhones(
        @Header("Authorization") token: String
    ): ResponsePhones

    @Tag("tasks-types")
    @Headers("Accept: application/json")
    @GET("$API_VERSION/task-types")
    suspend fun getTaskTypes(
        @Header("Authorization") token: String
    ): ResponseTaskTypes

    @Tag("photo-types")
    @Headers("Accept: application/json")
    @GET("$API_VERSION/photo-types")
    suspend fun getPhotoTypes(
        @Header("Authorization") token: String
    ): ResponsePhotoTypes

    @Tag("task")
    @Headers("Accept: application/json")
    @POST("$API_VERSION/tasks-facts")
    fun sendTask(
        @Header("Authorization") token: String,
        @Body body: TaskEvent
    ): Call<ResponseTask>

    /**
     * @param time [patternDateTimeZone]
     */
    @Tag("photo")
    @Multipart
    @Headers("Accept: application/json")
    @POST("$API_VERSION/photos")
    fun sendPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Query("time") time: String,
        @Query("event_id") id: Int,
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("type") type: Int,
        @Part("time") _time: RequestBody = time.toRequestBody(textType),
        @Part("event_id") _id: RequestBody? = id.toString().toRequestBody(textType),
        @Part("latitude") _lat: RequestBody = lat.toString().toRequestBody(textType),
        @Part("longitude") _lon: RequestBody = lon.toString().toRequestBody(textType),
        @Part("type") _type: RequestBody = type.toString().toRequestBody(textType)
    ): Call<ResponsePhoto>

    @Tag("logout")
    @POST("$API_VERSION/auth/close")
    fun logout(
        @Header("Authorization") token: String
    ): Call<Unit>

    @Tag("version")
    @Headers("Accept: application/json")
    @GET("version.json")
    suspend fun checkVersion(): ResponseVersion

    companion object {

        private const val API_VERSION = "v2"

        private val textType = "text/plain".toMediaTypeOrNull()
    }
}