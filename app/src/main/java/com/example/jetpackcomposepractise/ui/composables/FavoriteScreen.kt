package com.example.jetpackcomposepractise.ui.composables

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.jetpackcomposepractise.data.model.Product


@Composable
fun FavoriteScreen(favoriteDevice: List<Product>?, navController: NavHostController) {
    if (favoriteDevice.isNullOrEmpty()) {
        PlaceholderScreen(screenName = "No Favorite Item...")
    } else {
        ShowDeviceList(navController, favoriteDevice)
    }
}