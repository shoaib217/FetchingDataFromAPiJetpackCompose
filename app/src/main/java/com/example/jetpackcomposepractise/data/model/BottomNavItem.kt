package com.example.jetpackcomposepractise.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jetpackcomposepractise.MainActivity.Companion.CART_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.FAVORITE_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.PRODUCT_SCREEN

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem(PRODUCT_SCREEN, "Home", Icons.Default.Home)
    object Cart : BottomNavItem(CART_SCREEN, "Cart", Icons.Default.ShoppingCart)
    object Favorite : BottomNavItem(FAVORITE_SCREEN, "Favorite", Icons.Default.Favorite)
}