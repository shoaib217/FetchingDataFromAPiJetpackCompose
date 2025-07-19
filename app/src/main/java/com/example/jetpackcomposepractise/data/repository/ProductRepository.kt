package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.model.NetworkResponse

interface ProductRepository {
    suspend fun getProducts(): NetworkResponse
    // If you fetch single product from API, add:
    // suspend fun getProductById(productId: Int): Product?
}