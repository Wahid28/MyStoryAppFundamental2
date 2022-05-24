package com.example.mystoryapp.retrofit

import com.example.mystoryapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("stories?location=1")
    fun getStoriesWithLocation(
        @Header("Authorization") token: String
    ): Call<GetStoriesResponse>

    @GET("stories")
    suspend fun getStoriesWithPaging(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetStoriesResponse

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postUserLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<ImageUploadResponse>
}