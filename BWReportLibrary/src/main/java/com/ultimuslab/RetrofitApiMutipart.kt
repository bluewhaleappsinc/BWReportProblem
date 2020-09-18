package com.ultimuslab

import com.ultimuslab.bwreportlibrary.BuildConfig
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetrofitApiMutipart {


    @Multipart
    @POST("posts/{id}")
    fun uploadFile(@Path("id") id: Int, @Part file: MultipartBody.Part): Call<ResponseBody?>?

    @Headers(
        "Authorization: Basic QW5vbnltb3VzOlRlc3RAMTIzNDU2",
        "Content-Type: application/octet-stream"
    )
    @POST("uploads.json")
    fun uploadMediaToRedmine(@Body file: RequestBody): Call<UploadRes>?


    @Headers(
        "Authorization: Basic QW5vbnltb3VzOlRlc3RAMTIzNDU2",
        "Content-Type: application/json"
    )
    @POST("issues.json")
    fun createRedmineIssue(@Body body: RedmineIssueCreator): Call<ResponseBody>?

    @Multipart
    @POST("payment_medias/{id}")
    fun uploadPaymentMedia(
        @Header("access-token") token: String,
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody?>?


//    @Multipart
//    @POST("upload_media/{id}")
//    fun uploadMediaChat(
//        @Header("access-token") token: String,
//        @Path("id") id: String,
//        @Part file: MultipartBody.Part
//    ): Call<MediaUploadRes?>?

    companion object Factory {

        fun createMultipart(): RetrofitApiMutipart {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.multipartBaseUrl)
                .build()

            return retrofit.create(RetrofitApiMutipart::class.java)
        }
    }


}
