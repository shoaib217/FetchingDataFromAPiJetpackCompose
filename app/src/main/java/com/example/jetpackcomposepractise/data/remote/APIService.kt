package com.example.jetpackcomposepractise.data.remote

import com.example.jetpackcomposepractise.data.model.DeviceList
import retrofit2.Response
import retrofit2.http.GET

const val BASE_URL = "https://dummyjson.com/"

interface APIService {

    @GET("products")
    suspend fun getAllProducts(): Response<DeviceList?>

}