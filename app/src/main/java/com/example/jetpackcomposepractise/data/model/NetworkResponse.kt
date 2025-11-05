package com.example.jetpackcomposepractise.data.model

sealed class NetworkResponse {
    data object Success : NetworkResponse()
    data class Error(val message: String) : NetworkResponse()
}