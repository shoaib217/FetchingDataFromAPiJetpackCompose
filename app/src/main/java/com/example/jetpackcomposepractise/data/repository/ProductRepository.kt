package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.model.NetworkResponse
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.model.UserCartItem
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getFavoriteProducts(): Flow<List<Int>>
    fun getUserCartItem(): Flow<List<UserCartItem>>
    suspend fun syncProducts(): NetworkResponse
    suspend fun addToCart(productId: Int)
    suspend fun removeFromCart(productId: Int)
    suspend fun clearCartItem(productId: Int)
    suspend fun addProductToFavorites(productId: Int)
    suspend fun removeProductFromFavorites(productId: Int)

}