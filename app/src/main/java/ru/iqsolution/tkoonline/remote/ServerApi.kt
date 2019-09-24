@file:Suppress("unused")

package ru.iqsolution.tkoonline.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.iqsolution.tkoonline.remote.models.*

interface ServerApi {

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST
    suspend fun login(
        baseUrl: String,
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("block_code") blockCode: Int?,
        @Url url: String = "$baseUrl/auth"
    ): ResponseAuth

    /**
     * @param date [ru.iqsolution.tkoonline.PATTERN_DATE]
     */
    @Headers("Accept: application/json")
    @GET
    suspend fun getPlatforms(
        baseUrl: String,
        date: String,
        @Header("Authorization") token: String,
        @Url url: String = "$baseUrl/container-sites/$date"
    ): ResponsePlatforms

    @Headers("Accept: application/json")
    @GET
    suspend fun getPhotoTypes(
        baseUrl: String,
        @Header("Authorization") token: String,
        @Url url: String = "$baseUrl/photo-types"
    ): ResponseTypes

    @Headers("Accept: application/json")
    @POST
    suspend fun sendEvent(
        baseUrl: String,
        kpId: Int,
        @Header("Authorization") token: String,
        @Body body: RequestClean,
        @Url url: String = "$baseUrl/container-sites/$kpId/events"
    ): ResponseClean

    /**
     * @param time [ru.iqsolution.tkoonline.PATTERN_DATETIME]
     */
    @Multipart
    @Headers("Accept: application/json")
    @POST
    suspend fun sendPhoto(
        baseUrl: String,
        @Header("Authorization") token: String,
        @Part("kpId") kpId: RequestBody,
        @Part("type") type: RequestBody,
        @Part("time") time: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part photo: MultipartBody.Part,
        @Url url: String = "$baseUrl/container-sites/photos"
    ): ResponsePhoto

    @POST
    suspend fun logout(
        baseUrl: String,
        @Header("Authorization") token: String,
        @Url url: String = "$baseUrl/auth/close"
    ): ResponseClean
}