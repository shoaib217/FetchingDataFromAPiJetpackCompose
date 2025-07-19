package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.model.DeviceList

interface ProductRepository {
    suspend fun getProducts(): DeviceList?
    // If you fetch single product from API, add:
    // suspend fun getProductById(productId: Int): Product?
}