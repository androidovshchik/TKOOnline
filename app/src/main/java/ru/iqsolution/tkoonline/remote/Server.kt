@file:Suppress("unused")

package ru.iqsolution.tkoonline.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import ru.iqsolution.tkoonline.remote.api.*

interface Server {

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("auth")
    suspend fun login(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("block_code") blockCode: Int?
    ): ResponseAuth

    /**
     * @param date [ru.iqsolution.tkoonline.PATTERN_DATE]
     */
    @Headers("Accept: application/json")
    @GET("container-sites/{date}")
    suspend fun getPlatforms(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): ResponsePlatforms

    @Headers("Accept: application/json")
    @GET("photo-types")
    suspend fun getPhotoTypes(
        @Header("Authorization") token: String
    ): ResponseTypes

    @Headers("Accept: application/json")
    @POST("container-sites/{kp_id}/events")
    fun sendClean(
        @Header("Authorization") token: String,
        @Path("kp_id") kpId: Int,
        @Body body: RequestClean
    ): Call<ResponseClean>

    /**
     * @param time [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @Multipart
    @Headers("Accept: application/json")
    @POST("container-sites/photos")
    fun sendPhoto(
        @Header("Authorization") token: String,
        @Part("kpId") kpId: RequestBody,
        @Part("type") type: RequestBody,
        @Part("time") time: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ResponsePhoto>

    @POST("auth/close")
    suspend fun logout(
        @Header("Authorization") token: String
    ): ResponseClean
}