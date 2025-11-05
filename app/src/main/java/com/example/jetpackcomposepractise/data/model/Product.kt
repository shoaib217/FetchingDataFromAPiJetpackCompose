package com.example.jetpackcomposepractise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    val brand: String?,
    val category: String,
    val description: String?,
    val discountPercentage: Double,
    @PrimaryKey val id: Int,
    val images: List<String>,
    val price: Double,
    val rating: Double,
    val stock: Int,
    val thumbnail: String,
    val title: String?,
    var isFavorite: Boolean = false,
    var cartCount: Int = 0,
)