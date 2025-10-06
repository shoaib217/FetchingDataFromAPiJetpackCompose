package com.example.jetpackcomposepractise.ui.composables

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableIntStateOf
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
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val backStackEntry by navController.currentBackStackEntryAsState()

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    var displayMenuAppbar by remember {
        mutableStateOf(false)
    }

    when (backStackEntry?.destination?.route) {
        PRODUCT_SCREEN -> {
            selectedItemIndex = 0
        }

        CART_SCREEN -> {
            selectedItemIndex = 1
        }

        FAVORITE_SCREEN -> {
            selectedItemIndex = 2
        }
    }
    val showBottomBar = backStackEntry?.destination?.route == PRODUCT_SCREEN

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState)
    }, topBar = {
        TopAppBar(
            title = { Text(text = "Product Info") },
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
                AnimatedVisibility(showBottomBar) {
                    IconButton(
                        onClick = {
                            displayMenuAppbar = !displayMenuAppbar
                        },
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }
                AnimatedVisibility(
                    displayMenuAppbar,
                    enter = fadeIn(animationSpec = tween(400)),
                    exit = fadeOut(animationSpec = tween(400))
                ) {
                    DropdownMenu(
                        expanded = displayMenuAppbar, onDismissRequest = {
                            displayMenuAppbar = !displayMenuAppbar
                        }) {

                        categoryList?.forEach { categoryName ->
                            DropdownMenuItem(text = {
                                Text(text = categoryName)
                            }, onClick = {
                                clickActions.filterProductByCategory(categoryName)
                                displayMenuAppbar = !displayMenuAppbar
                            })
                        }
                    }

                }
            })
    }, bottomBar = {
        BottomBar(
            navItems = navItems,
            selectedItemIndex = selectedItemIndex,
            onItemSelected = { index, route ->
                selectedItemIndex = index
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
    }) { paddingValues ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navController = navController,
            startDestination = PRODUCT_SCREEN,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500))
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
                        onDismiss = { showAlertDialog = !showAlertDialog },
                        onConfirmation = {
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
                        product, clickActions, snackBarHostState
                    )
                }
            }
            composable(CART_SCREEN) {
                CartScreen(
                    cartItems = cartItems, clickActions
                )
            }
            composable(FAVORITE_SCREEN) {
                FavoriteScreen(favoriteDevice, navController)
            }
        }
    }
}
