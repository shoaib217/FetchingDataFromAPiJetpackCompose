package com.example.jetpackcomposepractise.data.model

data class DeviceList(
    val limit: Int,
    val products: List<Product>?,
    val skip: Int,
    val total: Int
)