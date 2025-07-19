package com.example.jetpackcomposepractise.data.model

sealed class NetworkResponse {
    data class Success(val productList: List<Product>) : NetworkResponse()
    data class Error(val message: String) : NetworkResponse()
}