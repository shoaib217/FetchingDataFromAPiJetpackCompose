package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.model.NetworkResponse
import com.example.jetpackcomposepractise.data.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun syncProducts(): NetworkResponse
    suspend fun addToCart(productId: Int)
    suspend fun removeFromCart(productId: Int)
    suspend fun clearCartItem(productId: Int)
    suspend fun markProductFavorite(productId: Int, isFavorite: Boolean)

}