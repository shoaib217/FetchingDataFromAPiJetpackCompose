package com.example.jetpackcomposepractise

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.SubcomposeAsyncImage
import com.example.jetpackcomposepractise.MainActivity.Companion.DETAIL_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.PRODUCT_SCREEN
import com.example.jetpackcomposepractise.MainActivity.Companion.TAG
import com.example.jetpackcomposepractise.data.model.DeviceList
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.ui.theme.JetpackComposePractiseTheme
import com.example.jetpackcomposepractise.ui.viewmodel.MainViewModel
import com.example.jetpackcomposepractise.ui.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
                val categoryList by
                mainViewModel.categoryList.collectAsState()

                ProductRoot(devices, deviceList, categoryList, object : ClickActions {
                    override fun filterProductByCategory(category: String) {
                        mainViewModel.filterProductByCategory(category)
                    }

                    override fun filterProductByType(selectedFilter: Filter?) {
                        mainViewModel.setFilterData(selectedFilter)
                    }

                    override fun onDismiss() {
                        this@MainActivity.onDismiss()
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
        val TAG: String = MainActivity::class.java.simpleName
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRoot(
    devices: UiState,
    deviceList: DeviceList?,
    categoryList: ArrayList<String>?,
    clickActions: ClickActions,
) {
    var selectedFilter by remember { mutableStateOf<Filter?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }
    var toolbarName by remember {
        mutableStateOf("Product Info")
    }
    var showAlertDialog by remember {
        mutableStateOf(false)
    }
    val navController = rememberNavController()
    var displayMenuAppbar by remember {
        mutableStateOf(false)
    }
    var displayMenuIcon by remember {
        mutableStateOf(true)
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(text = toolbarName) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    AnimatedVisibility(displayMenuIcon) {
                        IconButton(
                            onClick = {
                                displayMenuAppbar = !displayMenuAppbar
                            },
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }

                    }
                    AnimatedVisibility(displayMenuAppbar) {
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
        }) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = PRODUCT_SCREEN
        ) {
            composable(PRODUCT_SCREEN) {
                displayMenuIcon = true
                toolbarName = "Product Info"
                Column {
                    SingleSelectFilterChips(
                        filters = Filter.values(),
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
                    ExitDialog(onDismiss = { showAlertDialog = !showAlertDialog }) {
                        clickActions.onDismiss()
                    }
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
                displayMenuIcon = false
                val selectedProduct = deviceList?.products?.first { product ->
                    product.id == it.arguments?.getInt("id")
                }
                toolbarName = selectedProduct?.title ?: "Product Info"
                selectedProduct?.let {
                    ProductDetailScreen(
                        it
                    )
                }
            }
        }
    }
}


@Composable
fun MySnackBar(snackBarHostState: SnackbarHostState, message: String) {
    val scope = rememberCoroutineScope()
    LaunchedEffect("key1") {
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
fun ExitDialog(onDismiss: () -> Unit, onYes: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onYes) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "No")
            }
        },
        title = {
            Text(
                text = "Alert",
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(text = "Are you sure you want to exit?") },
    )
}


@Composable
fun ProductDetailScreen(
    product: Product,
) {
    Log.d(TAG, "device id -- ${product.id} / ${product.title}")
    Column(modifier = Modifier.padding(8.dp)) {
        ImageSlider(product.images)
        Text(text = "Details", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        CustomText("Category: ${product.category}")
        if (!product.brand.isNullOrEmpty() && !product.brand.equals("null", true)) {
            CustomText("Brand: ${product.brand}")
        }
        CustomText("Name: ${product.title}")
        CustomText("Price: $${product.price}")
        CustomText("Product Description: ${product.description}")
        CustomText("Rating: ${product.rating} / 5")
        CustomText("Remaining: ${product.stock}")
    }

}

@Composable
fun ImageSlider(images: List<String>) {
    LazyRow() {
        items(images) { url ->
            SubcomposeAsyncImage(
                model = url, contentDescription = "", modifier = Modifier
                    .padding(8.dp)
                    .size(350.dp, 250.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
                imageLoader = MyApplication.imageLoader,
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                })

        }
    }

}

/**
 * product list screen which contains product
 * @param[navController] for navigation between screens
 */
@Composable
fun ProductScreen(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    result: UiState,
    deviceList: DeviceList?,

    ) {
    when (result) {
        is UiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
            println("Under Loading")
        }

        is UiState.Error -> {
            MySnackBar(snackBarHostState, result.message)
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
    deviceList: DeviceList?,
) {
    Log.d(TAG, "data $deviceList")

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        deviceList?.products?.let { prod ->
            items(prod) { product ->
                DeviceCard(product, navController)
            }
        }
    }
}

@Composable
fun DeviceCard(product: Product, navController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(12.dp)
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .size(250.dp, 300.dp)
                .verticalScroll(rememberScrollState())
                .clickable(
                    onClick = {
                        navController.navigate(MainActivity.DETAIL_SCREEN + product.id)
                    }
                ),
        ) {
            SubcomposeAsyncImage(
                model = product.images[0],
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .size(250.dp, 150.dp)
                    .clip(RoundedCornerShape(20)),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                },
            )
            if (!product.brand.isNullOrEmpty() && !product.brand.equals("null", true)) {
                CustomText("Brand: ${product.brand}")
            }
            if (!product.title.isNullOrEmpty() && !product.title.equals("null", true)) {
                CustomText("Title: ${product.title}")
            }
            CustomText("Price: $${product.price}")
        }
    }
}

/**
 * custom text style
 */
@Composable
fun CustomText(value: String) {
    Text(text = value, fontSize = 18.sp, modifier = Modifier.padding(4.dp))
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
                    onClick = {
                        onFilterSelected(filter)
                    },
                    label = { Text(filter.value) },
                    leadingIcon = if (selectedFilter == filter) {
                        { Icon(imageVector = Icons.Filled.Done, contentDescription = "Selected") }
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
}


@Composable
fun TemplateScreen(){

}