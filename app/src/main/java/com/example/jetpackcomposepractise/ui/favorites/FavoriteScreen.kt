package com.example.jetpackcomposepractise.ui.favorites

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.ui.shared_widget.ShowDeviceList
import com.example.jetpackcomposepractise.ui.shared_widget.PlaceholderScreen


@Composable
fun FavoriteScreen(favoriteDevice: List<Product>?, navController: NavHostController) {
    if (favoriteDevice.isNullOrEmpty()) {
        PlaceholderScreen(screenName = "No Favorite Item...")
    } else {
        ShowDeviceList(navController, favoriteDevice)
    }
}