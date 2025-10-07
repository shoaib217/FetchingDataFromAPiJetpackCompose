package com.example.jetpackcomposepractise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.jetpackcomposepractise.data.model.ClickActions
import com.example.jetpackcomposepractise.data.model.Filter
import com.example.jetpackcomposepractise.ui.root_screen.ProductRoot
import com.example.jetpackcomposepractise.ui.theme.JetpackComposePractiseTheme
import com.example.jetpackcomposepractise.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePractiseTheme {

                // A surface container using the 'background' color from the theme

                val uiState by mainViewModel.uiState.collectAsState()
                val deviceList by mainViewModel.deviceList.collectAsState()
                val cartItems by mainViewModel.cartItems.collectAsState()
                val favoriteDevice by mainViewModel.favoriteDevice.collectAsState()
                val categoryList by mainViewModel.categoryList.collectAsState()


                ProductRoot(
                    uiState,
                    deviceList,
                    categoryList,
                    favoriteDevice,
                    cartItems,
                    object : ClickActions {
                        override fun filterProductByCategory(category: String) {
                            mainViewModel.filterProductByCategory(category)
                        }

                        override fun filterProductByType(selectedFilter: Filter?) {
                            mainViewModel.setFilterData(selectedFilter)
                        }

                        override fun onDismiss() {
                            this@MainActivity.onDismiss()
                        }

                        override fun onAddQuantityItem(productId: Int) {
                            mainViewModel.addToCart(productId)
                        }

                        override fun onToggleFavorite(productId: Int) {
                            mainViewModel.markProductAsFavorite(productId)
                        }

                        override fun onRemoveQuantityItem(productId: Int) {
                            mainViewModel.removeFromCart(productId)
                        }

                        override fun onRemoveFromCart(productId: Int) {
                            mainViewModel.removeItemFromCart(productId)
                        }
                    })

            }
        }
    }

    private fun onDismiss() {
        this.finishAffinity()
    }

    companion object {
        const val PRODUCT_SCREEN = "productScreen"
        const val DETAIL_SCREEN = "detailScreen/"
        const val CART_SCREEN = "cartScreen"
        const val FAVORITE_SCREEN = "favoriteScreen"
    }
}
