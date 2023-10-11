package com.example.jetpackcomposepractise.data

data class DeviceList(
    val limit: Int,
    val products: List<Product>?,
    val skip: Int,
    val total: Int
)