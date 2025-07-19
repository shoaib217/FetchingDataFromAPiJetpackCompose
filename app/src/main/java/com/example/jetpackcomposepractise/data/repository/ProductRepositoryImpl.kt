package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.model.DeviceList
import com.example.jetpackcomposepractise.data.remote.APIService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton // So Hilt provides a single instance
class ProductRepositoryImpl @Inject constructor(
    private val apiService: APIService,
) : ProductRepository {

    override suspend fun getProducts(): DeviceList? {
        return apiService.getAllProducts()
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