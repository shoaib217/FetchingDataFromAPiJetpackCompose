package com.example.jetpackcomposepractise.ui.root_screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcomposepractise.MainActivity.Companion.CART_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.DETAIL_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.FAVORITE_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.PRODUCT_SCREEN
import com.example.jetpackcomposepractise.TAG
import com.example.jetpackcomposepractise.data.model.BottomNavItem
import com.example.jetpackcomposepractise.data.model.ClickActions
import com.example.jetpackcomposepractise.data.model.Filter
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.model.UiState
import com.example.jetpackcomposepractise.ui.cart.CartScreen
import com.example.jetpackcomposepractise.ui.favorites.FavoriteScreen
import com.example.jetpackcomposepractise.ui.product_detail.ProductDetailScreen
import com.example.jetpackcomposepractise.ui.products.ProductScreen
import com.example.jetpackcomposepractise.ui.products.SingleSelectFilterChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRoot(
    uiState: UiState,
    deviceList: List<Product>?,
    categoryList: List<String>?,
    favoriteDevice: List<Product>?,
    cartItems: List<Product>?,
    clickActions: ClickActions,
) {
    var selectedFilter by remember { mutableStateOf<Filter?>(null) }
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    val navItems = listOf(BottomNavItem.Home, BottomNavItem.Cart, BottomNavItem.Favorite)

    val backStackEntry by navController.currentBackStackEntryAsState()

    var showAlertDialog by remember {
        mutableStateOf(false)
    }


    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = backStackEntry?.destination?.route == PRODUCT_SCREEN

    val title = when(currentRoute) {
        PRODUCT_SCREEN -> "Products"
        CART_SCREEN -> "Cart"
        FAVORITE_SCREEN -> "Favorites"
        else -> ""
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState)
    }, topBar = {
        TopAppBar(
            title = { Text(text = title) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            navigationIcon = {
                if (navController.previousBackStackEntry != null) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = {
                ActionItems(showBottomBar, categoryList, clickActions)
            })
    }, bottomBar = {
        val selectedItemIndex = navItems.indexOfFirst { it.route == currentRoute }

        // Only show the bottom bar if the route is one of the nav items.
        AnimatedVisibility(
            visible = selectedItemIndex != -1,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(500)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
        ) {
            BottomBar(
                navItems = navItems,
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { index, route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }) { paddingValues ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navController = navController,
            startDestination = PRODUCT_SCREEN,
            enterTransition = {
                // Forward navigation: New screen slides in from the right (Start direction)
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                // Forward navigation: Old screen slides out to the left (Start direction)
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = {
                // Back navigation: Previous screen slides in from the left (End direction)
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                // Back navigation (including predictive back): Current screen slides out to the right (End direction)
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(500)
                )
            }

        ) {
            composable(PRODUCT_SCREEN) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    SingleSelectFilterChips(
                        filters = Filter.entries.toTypedArray(),
                        selectedFilter,
                        onFilterSelected = {
                            selectedFilter = if (it == selectedFilter) null else it
                            Log.d(TAG, "filter selected: ${selectedFilter?.name} ")
                            clickActions.filterProductByType(selectedFilter)
                        })
                    ProductScreen(
                        navController, snackBarHostState, uiState, deviceList
                    )

                }
                if (showAlertDialog) {
                    ExitDialog(
                        onDismiss = { showAlertDialog = false },
                        onConfirmation = {
                            showAlertDialog = false
                            clickActions.onDismiss()
                        })
                }
                BackHandler {
                    Log.d(TAG, "back press under scaffold")
                    showAlertDialog = !showAlertDialog
                }
            }

            composable(
                "$DETAIL_SCREEN{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it }, animationSpec = tween(100)
                    )
                }) {
                val selectedProduct = deviceList?.firstOrNull { product ->
                    product.id == it.arguments?.getInt("id")
                }
                selectedProduct?.let { product ->
                    ProductDetailScreen(
                        product, clickActions, snackBarHostState, onNavigateToCart = {
                            navController.navigate(CART_SCREEN)
                        }
                    )
                }
            }
            composable(CART_SCREEN) {
                CartScreen(
                    cartItems = cartItems, clickActions, navigateToDetailScreen = {
                        navController.navigate("$DETAIL_SCREEN$it")
                    }
                )
            }
            composable(FAVORITE_SCREEN) {
                FavoriteScreen(favoriteDevice, navController)
            }
        }
    }
}
