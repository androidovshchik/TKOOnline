@file:Suppress("unused")

package ru.iqsolution.tkoonline.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.http.*
import ru.iqsolution.tkoonline.local.entities.CleanEventToken
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
     * @param date [ru.iqsolution.tkoonline.extensions.PATTERN_DATE]
     */
    @Tag("platforms")
    @Headers("Accept: application/json")
    @GET("v1/container-sites/{date}?skip-response")
    suspend fun getPlatforms(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): ResponsePlatforms

    @Tag("photos")
    @Headers("Accept: application/json")
    @GET("v1/photo-types?skip-response")
    suspend fun getPhotoTypes(
        @Header("Authorization") token: String
    ): ResponseTypes

    @Tag("clean")
    @Headers("Accept: application/json")
    @POST("v1/container-sites/{kp_id}/events")
    fun sendClean(
        @Header("Authorization") token: String,
        @Path("kp_id") kpId: Int,
        @Body body: CleanEventToken
    ): Call<ResponseClean>

    /**
     * @param time [ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE]
     */
    @Tag("photo")
    @Multipart
    @Headers("Accept: application/json")
    @POST("v1/container-sites/photos?skip-request")
    fun sendPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Query("cs_id") kpId: Int?,
        @Query("type") type: Int,
        @Query("time") time: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Part("cs_id") _kpId: RequestBody? = kpId?.toString()?.toRequestBody(TEXT_TYPE),
        @Part("type") _type: RequestBody = type.toString().toRequestBody(TEXT_TYPE),
        @Part("time") _time: RequestBody = time.toRequestBody(TEXT_TYPE),
        @Part("latitude") _lat: RequestBody = lat.toString().toRequestBody(TEXT_TYPE),
        @Part("longitude") _lon: RequestBody = lon.toString().toRequestBody(TEXT_TYPE)
    ): Call<ResponsePhoto>

    @Tag("logout")
    @POST("v1/auth/close")
    fun logout(
        @Header("Authorization") token: String
    ): Call<Unit>

    @Tag("version")
    @GET("version.json")
    suspend fun checkVersion(): ResponseVersion

    companion object {

        private val TEXT_TYPE = "text/plain".toMediaTypeOrNull()
    }
}