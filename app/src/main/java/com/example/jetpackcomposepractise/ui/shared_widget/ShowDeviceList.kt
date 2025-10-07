package com.example.jetpackcomposepractise.ui.shared_widget

import android.util.Log
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.jetpackcomposepractise.TAG
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.ui.products.DeviceCard

@Composable
fun ShowDeviceList(
    navController: NavHostController,
    deviceList: List<Product>?,
) {
    Log.d(TAG, "data $deviceList")

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        deviceList?.let { prod ->
            items(prod, key = { product ->
                product.id
            }) { product ->
                DeviceCard(product, navController)
            }
        }
    }
}
