@file:Suppress("unused")

package ru.iqsolution.tkoonline.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import ru.iqsolution.tkoonline.remote.api.*

interface Server {

    @Tag("login")
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("v1/auth")
    suspend fun login(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("block_code") blockCode: Int?
    ): ResponseAuth

    /**
     * @param date [ru.iqsolution.tkoonline.PATTERN_DATE]
     */
    @Tag("platforms")
    @Headers("Accept: application/json")
    @GET("v1/container-sites/{date}")
    suspend fun getPlatforms(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): ResponsePlatforms

    @Tag("photos")
    @Headers("Accept: application/json")
    @GET("v1/photo-types")
    suspend fun getPhotoTypes(
        @Header("Authorization") token: String
    ): ResponseTypes

    @Tag("clean")
    @Headers("Accept: application/json")
    @POST("v1/container-sites/{kp_id}/events")
    fun sendClean(
        @Header("Authorization") token: String,
        @Path("kp_id") kpId: Int,
        @Body body: RequestClean
    ): Call<ResponseClean>

    /**
     * @param time [ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE]
     */
    @Tag("photo")
    @Multipart
    @Headers("Accept: application/json")
    @POST("v1/container-sites/photos")
    fun sendPhoto(
        @Header("Authorization") token: String,
        @Part("cs_id") kpId: RequestBody?,
        @Part("type") type: RequestBody,
        @Part("time") time: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ResponsePhoto>

    @Tag("logout")
    @POST("v1/auth/close")
    fun logout(
        @Header("Authorization") token: String
    ): Call<Unit>

    @Tag("version")
    @GET("version.json")
    suspend fun checkVersion(): ResponseVersion
}