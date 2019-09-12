@file:Suppress("unused")

package ru.iqsolution.tkoonline.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.iqsolution.tkoonline.data.models.ResponseAuth
import ru.iqsolution.tkoonline.data.models.ResponseContainers

interface ServerApi {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("auth")
    suspend fun login(@Field("login") login: String, @Field("password") password: String, @Field("block_code") blockCode: Int?): ResponseAuth

    @Headers("Accept: application/json")
    @GET("container-sites/{date}")
    suspend fun getContainers(@Header("Authorization") token: String, @Path("date") date: String): ResponseContainers

    @Headers("Accept: application/json")
    @GET("photo-types")
    suspend fun getPhotoTypes(): ResponseContainers



    @GET("backgrounds/backgrounds.json")
    fun jsonBackgrounds(): Call<List<String>>

    @POST("api/payment.php")
    fun processPayment(@Header("Authorization") token: String, @Body body: MyPayment): Call<ResponsePayment>

    @Multipart
    @POST("api/picture.php")
    fun processPicture(@Header("Authorization") token: String, @Part image: MultipartBody.Part): Call<ResponseBody>
}