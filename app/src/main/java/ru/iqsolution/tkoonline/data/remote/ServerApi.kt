@file:Suppress("unused")

package ru.iqsolution.tkoonline.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import ru.iqsolution.tkoonline.data.models.*

interface ServerApi {

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("auth")
    suspend fun login(@Field("login") login: String, @Field("password") password: String, @Field("block_code") blockCode: Int?): ResponseAuth

    @Headers("Accept: application/json")
    @GET("container-sites/{date}")
    suspend fun getContainers(@Header("Authorization") token: String, @Path("date") date: String): ResponseContainers

    @Headers("Accept: application/json")
    @GET("photo-types")
    suspend fun getPhotoTypes(): ResponseTypes

    @Headers("Accept: application/json")
    @POST("container-sites/{kp_id}/events")
    suspend fun sendEvent(@Header("Authorization") token: String, @Path("kp_id") kpId: Int, @Body body: RequestEvent): ResponseEvent

    @Multipart
    @POST("container-sites/{kp_id}/photos")
    suspend fun sendPhoto(
        @Header("Authorization") token: String, @Path("kp_id") kpId: Int, @Part("time") time: ResponseBody,
        @Part("type") type: ResponseBody, @Part photo: MultipartBody.Part
    ): ResponseBody
}