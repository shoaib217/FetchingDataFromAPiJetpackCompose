package com.example.jetpackcomposepractise.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://dummyjson.com/"

interface APIService {

    @GET("products")
    suspend fun getAllProducts(): DeviceList?

    companion object{
        private var apiService: APIService? = null
        fun getInstance() : APIService{
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}