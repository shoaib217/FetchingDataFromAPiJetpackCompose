package com.example.jetpackcomposepractise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
data class UserFavoriteProduct(
    @PrimaryKey val productId: Int,
)