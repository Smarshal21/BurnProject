package com.example.burnproject

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MyApi {
    @POST("upload")
    fun uploadImage(@Body request: ImageRequest): Call<ApiResponse>

    @GET("endpoint")
    fun getFloat(): Call<Float>

}

data class ImageRequest(val image: String)
data class ApiResponse(val success: Boolean, val message: String)
