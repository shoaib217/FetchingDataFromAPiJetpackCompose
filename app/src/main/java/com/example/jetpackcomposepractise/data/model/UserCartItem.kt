package com.example.jetpackcomposepractise.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_cart_item")
data class UserCartItem(
    @PrimaryKey val productId: Int,
    val cartCount: Int
)
