package com.example.jetpackcomposepractise.ui.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.model.UiState
import com.example.jetpackcomposepractise.ui.shared_widget.ShowDeviceList

/**
 * product list screen which contains product
 * @param[navController] for navigation between screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    result: UiState,
    deviceList: List<Product>?,
) {
    when (result) {
        is UiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
            println("Under Loading")
        }

        is UiState.Error -> {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                MySnackBar(snackBarHostState, result.message, (0..100).random())
            }
        }

        is UiState.Success -> {
            println("Under Success")
            ShowDeviceList(navController, deviceList)
        }
    }
}
