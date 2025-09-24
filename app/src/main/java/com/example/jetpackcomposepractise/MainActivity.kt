package com.example.jetpackcomposepractise

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.SubcomposeAsyncImage
import com.example.jetpackcomposepractise.MainActivity.Companion.CART_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.DETAIL_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.FAVORITE_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.PRODUCT_SCREEN
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.ui.theme.JetpackComposePractiseTheme
import com.example.jetpackcomposepractise.ui.viewmodel.MainViewModel
import com.example.jetpackcomposepractise.ui.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

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

                val devices by mainViewModel.devices.collectAsState()
                val deviceList by mainViewModel.deviceList.collectAsState()
                val favoriteDevice by mainViewModel.favoriteDevice.collectAsState()
                val categoryList by
                mainViewModel.categoryList.collectAsState()


                ProductRoot(devices, deviceList, categoryList,favoriteDevice, object : ClickActions {
                    override fun filterProductByCategory(category: String) {
                        mainViewModel.filterProductByCategory(category)
                    }

                    override fun filterProductByType(selectedFilter: Filter?) {
                        mainViewModel.setFilterData(selectedFilter)
                    }

                    override fun onDismiss() {
                        this@MainActivity.onDismiss()
                    }

                    override fun onAddToCart(productId: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onToggleFavorite(productId: Int) {
                        mainViewModel.markProductAsFavorite(productId)
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

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem(PRODUCT_SCREEN, "Home", Icons.Default.Home)
    object Cart : BottomNavItem(CART_SCREEN, "Cart", Icons.Default.ShoppingCart)
    object Favorite : BottomNavItem(FAVORITE_SCREEN, "Favorite", Icons.Default.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRoot(
    devices: UiState,
    deviceList: List<Product>?,
    categoryList: ArrayList<String>?,
    favoriteDevice: List<Product>?,
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

    when(backStackEntry?.destination?.route) {
        PRODUCT_SCREEN ->{
            selectedItemIndex = 0
        }
        CART_SCREEN ->{
            selectedItemIndex = 1
        }
        FAVORITE_SCREEN ->{
            selectedItemIndex = 2
        }
    }
    val showBottomBar = backStackEntry?.destination?.route == PRODUCT_SCREEN
            || backStackEntry?.destination?.route == CART_SCREEN
            || backStackEntry?.destination?.route == FAVORITE_SCREEN

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
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
                    AnimatedVisibility(displayMenuAppbar,
                        enter = fadeIn(animationSpec = tween(400)),
                        exit = fadeOut(animationSpec = tween(400))) {
                        DropdownMenu(
                            expanded = displayMenuAppbar,
                            onDismissRequest = {
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
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
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
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navController = navController,
            startDestination = PRODUCT_SCREEN,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) },
            popEnterTransition = { fadeIn(animationSpec = tween(500)) },
            popExitTransition = { fadeOut(animationSpec = tween(500)) }


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
                        navController, snackBarHostState,
                        devices,
                        deviceList
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
                        targetOffsetX = { it },
                        animationSpec = tween(100)
                    )
                }
            ) {
                val selectedProduct = deviceList?.firstOrNull { product ->
                    product.id == it.arguments?.getInt("id")
                }
                selectedProduct?.let { product ->
                    ProductDetailScreen(
                        product,
                        clickActions
                    )
                }
            }
            composable(CART_SCREEN) {
                PlaceholderScreen(screenName = "Cart")
            }
            composable(FAVORITE_SCREEN) {
                if (favoriteDevice.isNullOrEmpty()) {
                PlaceholderScreen(screenName = "No Favorite Item...")
                } else {
                    ShowDeviceList(navController, favoriteDevice)
                }

            }
        }
    }
}


@Composable
fun PlaceholderScreen(screenName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = screenName, style = MaterialTheme.typography.headlineMedium)
    }
}


@Composable
fun MySnackBar(snackBarHostState: SnackbarHostState, message: String, random: Int) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(random) {
        scope.launch {
            val snackBarResult = snackBarHostState
                .showSnackbar(
                    message = message,
                    actionLabel = "close",
                    duration = SnackbarDuration.Short
                )

            when (snackBarResult) {
                SnackbarResult.Dismissed -> {
                    Log.d(TAG, "Dismissed")
                }

                SnackbarResult.ActionPerformed -> {
                    Log.d(TAG, "ActionPerformed")
                }
            }
        }
    }
}

@Composable
fun ExitDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit, // Renamed from onYes for clarity, standard practice
    // Optional parameters for more flexibility
    dialogTitle: String = "Alert", // Consider using stringResource(R.string.alert_dialog_title)
    dialogText: String = "Are you sure you want to exit?", // Consider using stringResource(R.string.exit_dialog_message)
    confirmButtonText: String = "Yes", // Consider using stringResource(R.string.yes)
    dismissButtonText: String = "No", // Consider using stringResource(R.string.no)
    icon: ImageVector? = null, // Optional: For adding an icon to the dialog
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.headlineSmall, // Use a theme typography style
                color = MaterialTheme.colorScheme.onSurface // Color for text on the dialog's surface
            )
        },
        text = {
            Text(
                text = dialogText,
                style = MaterialTheme.typography.bodyMedium, // Use a theme typography style
                color = MaterialTheme.colorScheme.onSurfaceVariant // Slightly less prominent color for body
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(
                    text = confirmButtonText,
                    // TextButton text color defaults to MaterialTheme.colorScheme.primary, which is usually correct.
                    // You could explicitly set it: color = MaterialTheme.colorScheme.primary
                    fontWeight = FontWeight.SemiBold // Optional: to make action text slightly bolder
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissButtonText
                    // color = MaterialTheme.colorScheme.primary
                )
            }
        },
        // You can also theme the dialog container itself if needed,
        // though defaults are usually good.
        // containerColor = MaterialTheme.colorScheme.surface,
        // titleContentColor = MaterialTheme.colorScheme.onSurface,
        // textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}


@OptIn(ExperimentalMaterial3Api::class) // For Card and other Material 3 components
@Composable
fun ProductDetailScreen(
    product: Product,
    actions: ClickActions, // Optional actions
) {
    Log.d(TAG, "ProductDetailScreen for: ${product.title}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Make the whole screen scrollable if content exceeds screen height
            .padding(bottom = 16.dp) // Padding at the bottom for scrollable content
    ) {
        // --- Image Section ---
        ImageSlider(
            images = product.images,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Adjust height as needed
        )

        // --- Core Info & Actions Section ---
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            // Title
            Text(
                text = product.title ?: "Product Name",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Rating Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%.2f", product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107), // Gold color for star
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.rating} / 5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stock Information
            if (product.stock > 0) {
                Text(
                    text = "In Stock: ${product.stock} units remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary // Or a success color
                )
            } else {
                Text(
                    text = "Out of Stock",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Action Buttons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { actions.onAddToCart(product.id) },
                    modifier = Modifier.weight(1f),
                    enabled = product.stock > 0 // Disable if out of stock
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Add to Cart", modifier = Modifier.size(
                        ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Add to Cart")
                }
                OutlinedIconButton(
                    // Using OutlinedIconButton for a secondary action look
                    onClick = { actions.onToggleFavorite(product.id) },
                    // border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Default outline
                ) {
                    if (product.isFavorite) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
                    } else {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // --- Detailed Information Section ---
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(
                text = "Product Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            DetailItem("Category", product.category)

            if (!product.brand.isNullOrEmpty() && !product.brand.equals("null", ignoreCase = true)) {
                DetailItem("Brand", product.brand)
            }

            // Description - making it expandable or more prominent
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
            )
            Text(
                text = product.description ?: "No description available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.5 // Better line spacing for readability
            )
        }
    }
}



@Composable
private fun DetailItem(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.4f) // Adjust weight as needed
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}

// Ensure your ImageSlider is adapted to take a Modifier if it wasn't already
@Composable
fun ImageSlider(images: List<String>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier) { // Apply modifier here
        items(images) { url ->
            SubcomposeAsyncImage(
                model = url,
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillParentMaxHeight() // Fill the height provided by the parent (e.g., 300.dp from Column)
                    .aspectRatio(1f) // Maintain aspect ratio, adjust as needed e.g. 16/9f
                    .padding(horizontal = 4.dp), // Some spacing between images
                contentScale = ContentScale.Crop,
                loading = {
                    /* ... */
                }
            )
        }
    }
}

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
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
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

@Composable
fun DeviceCard(product: Product, navController: NavHostController) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp // Slightly reduced elevation can look cleaner
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp), // Consistent corner rounding
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 6.dp
            ) // Padding around the card itself in the grid
            .clickable(
                onClick = {
                    navController.navigate(DETAIL_SCREEN + product.id)
                }
            )
            .wrapContentHeight() // Allow card to grow based on content
    ) {
        // Explicit Column to arrange content within the card
        Column(
            modifier = Modifier
                .padding(12.dp) // Inner padding for the content
        ) {
            SubcomposeAsyncImage(
                model = product.images.getOrNull(0),
                contentDescription = product.title ?: "Product Image", // Accessibility
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // Slightly more height for image
                    .clip(RoundedCornerShape(8.dp)),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop, // Crop usually works well for fixed height images
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                },
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!product.brand.isNullOrEmpty() && !product.brand.equals("null", true)) {
                CustomText(
                    value = "Brand: ${product.brand}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (!product.title.isNullOrEmpty() && !product.title.equals("null", true)) {
                CustomText(
                    value = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            CustomText(
                value = "₹${String.format(Locale.ENGLISH, "%.2f", product.price)}", // Just the price for cleaner look
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * custom text style
 */
@Composable
fun CustomText(
    value: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified, // Defaults to LocalContentColor if not specified
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        text = value,
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = overflow
    )
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposePractiseTheme {
        Greeting("Android")
    }
}


enum class Filter(val value: String) {
    PRICE("Price"),
    RATING("Rating"),
    STOCK("Stock")
}

@Composable
fun SingleSelectFilterChips(
    filters: Array<Filter>,
    selectedFilter: Filter?,
    onFilterSelected: (Filter?) -> Unit,
) {

    Column {
        Row {
            filters.forEach { filter ->
                FilterChip(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    selected = selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        // ... other colors as needed
                    ),
                    onClick = {
                        onFilterSelected(filter)
                    },
                    label = { Text(filter.value) },
                    leadingIcon = if (selectedFilter == filter) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

interface ClickActions {
    fun filterProductByCategory(category: String)

    fun filterProductByType(selectedFilter: Filter?)

    fun onDismiss()

    fun onAddToCart(productId: Int)
    fun onToggleFavorite(productId: Int)

}


@Composable
fun BottomBar(
    navItems: List<BottomNavItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier, // Allow passing an external modifier
) {
    // This outer Box handles the overall positioning (floating effect) and padding.
    Box(
        modifier = modifier // Apply external modifier first
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Padding for the floating effect
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- Blur Modifier Definition ---
        // This modifier is created once and reused.
        // It's conditional on the API level.
        val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier.graphicsLayer {
                renderEffect = BlurEffect(
                    radiusX = 20f,
                    radiusY = 20f,
                    edgeTreatment = TileMode.Decal
                )
                // Ensure the layer itself is clipped to prevent blur bleeding outside the shape.
                clip = true
            }
        } else {
            Modifier // Empty modifier for older versions (no blur)
        }

        // --- Blurred Background Surface ---
        // This Box provides the themed, clipped, and conditionally blurred surface.
        // Its height matches the standard NavigationBar height.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp)) // Defines the shape of the glass
                .background(
                    // Use a semantic color from the theme for the glass background.
                    // Adjust alpha for desired translucency.
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
                )
                .then(blurModifier) // Apply the blur effect to this background surface
        )
        // This Box is intentionally empty; its background IS the visual content that gets blurred.
        // The blur effect applies to what's visually BEHIND this Box in the UI hierarchy.

        // --- NavigationBar on Top ---
        // This sits visually on top of the blurred background surface.
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                // Clip the NavigationBar to the same shape as the background for visual consistency.
                .clip(RoundedCornerShape(32.dp)),
            // Container color is transparent to let the blurred background Box show through.
            containerColor = Color.Transparent,
            // Tonal elevation is not needed here as the visual effect is achieved by the background Box.
            tonalElevation = 0.dp
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedItemIndex == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(index, item.route) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                            // Icon tint is handled by NavigationBarItemDefaults based on selection state
                        )
                    },
                    label = {
                        Text(
                            text = item.label
                            // Text color is handled by NavigationBarItemDefaults
                        )
                    },
                    // Use semantic theme colors for selected and unselected states.
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // alwaysShowLabel = true // Consider if labels should always be visible or only for selected
                )
            }
        }
    }
}