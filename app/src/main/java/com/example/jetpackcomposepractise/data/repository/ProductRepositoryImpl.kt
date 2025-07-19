package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.model.DeviceList
import com.example.jetpackcomposepractise.data.model.NetworkResponse
import com.example.jetpackcomposepractise.data.remote.APIService
import com.example.jetpackcomposepractise.util.NetworkConnectivityHelper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import retrofit2.Response

@Singleton // So Hilt provides a single instance
class ProductRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val networkConnectivityHelper: NetworkConnectivityHelper
) : ProductRepository {

    override suspend fun getProducts(): NetworkResponse {
        if (!networkConnectivityHelper.isNetworkAvailable()) {
            return NetworkResponse.Error("No internet connection")
        }
        val response = apiService.getAllProducts()
        return handleResponse(response)
    }

    private fun handleResponse(response: Response<DeviceList?>): NetworkResponse {
        if (response.isSuccessful) {
            val productList = response.body()?.products
            return if (productList != null) {
                NetworkResponse.Success(productList)
            } else {
                NetworkResponse.Error("No products found")
            }
        } else {
            return NetworkResponse.Error(response.message())
        }
    }


    // Example if fetching single product from API
    // override suspend fun getProductById(productId: Int): Product? {
    //     return try {
    //         apiService.getProductById(productId)
    //     } catch (e: Exception) {
    //         // Handle error, e.g., product not found
    //         null
    //     }
    // }
}