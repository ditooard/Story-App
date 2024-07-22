package com.ditooard.aplikasistoryapp.api

import com.ditooard.aplikasistoryapp.ui.register.DataAccountLogin
import com.ditooard.aplikasistoryapp.ui.register.DataAccountRegist
import com.ditooard.aplikasistoryapp.ui.register.DetailResponse
import com.ditooard.aplikasistoryapp.ui.register.LoginResponse
import com.ditooard.aplikasistoryapp.ui.register.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("register")
    fun registerUser(@Body registrationData: DataAccountRegist): Call<DetailResponse>

    @POST("login")
    fun loginUser(@Body loginData: DataAccountLogin): Call<LoginResponse>

    @GET("stories")
    fun fetchStories(
        @Header("Authorization") authToken: String,
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Part imageFile: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") authToken: String
    ): Call<DetailResponse>
}
