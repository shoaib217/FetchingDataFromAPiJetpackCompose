package com.example.jetpackcomposepractise

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.jetpackcomposepractise.MainActivity.Companion.TAG
import com.example.jetpackcomposepractise.data.Product
import com.example.jetpackcomposepractise.ui.theme.JetpackComposePractiseTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePractiseTheme {
                val navController = rememberNavController()
                val displayMenuAppbar = remember {
                    mutableStateOf(false)
                }
                // A surface container using the 'background' color from the theme
                val toolbarName = remember {
                    mutableStateOf("Product Info")
                }
                Scaffold(topBar = {
                    TopAppBar(
                        title = { Text(text = toolbarName.value) },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = {
                            IconButton(
                                onClick = {
                                    displayMenuAppbar.value = !displayMenuAppbar.value
                                },
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }

                            DropdownMenu(
                                expanded = displayMenuAppbar.value,
                                onDismissRequest = {
                                    displayMenuAppbar.value = !displayMenuAppbar.value
                                }) {
                                val categoryList = mainViewModel.categoryList.collectAsState()
                                val categoryNames : MutableList<String> = setOf("All").toMutableList()
                                categoryNames.addAll(categoryList.value?.keys?.toMutableList() ?: emptyList())
                                categoryNames.forEach { categoryName ->
                                    DropdownMenuItem(text = {
                                        Text(text = categoryName)
                                    }, onClick = {
                                        mainViewModel.setFilterData(categoryName)
                                        displayMenuAppbar.value = !displayMenuAppbar.value
                                    })
                                }
                            }
                        }
                    )
                }) { paddingValues ->
                    NavHost(navController = navController, startDestination = productScreen) {
                        composable(productScreen) {
                            toolbarName.value = "Product Info"
                            ProductScreen(
                                mainViewModel = mainViewModel,
                                applicationContext = applicationContext,
                                paddingValues, navController
                            )
                        }
                        composable(
                            "$detailScreen{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            ProductDetailScreen(
                                mainViewModel,
                                it.arguments?.getInt("id"),
                                navController
                            ) { name ->
                                toolbarName.value = name
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val productScreen = "productScreen"
        const val detailScreen = "detailScreen/"
        val TAG: String = MainViewModel::class.java.simpleName
    }
}


@Composable
fun ProductDetailScreen(
    mainViewModel: MainViewModel,
    id: Int?,
    navController: NavHostController,
    setToolbarName: (String) -> Unit,
) {
    val product = mainViewModel.getProduct(id)
    product?.let { product ->
        Log.d(TAG, "device id -- ${product.id} / ${product.title}")
        setToolbarName(product.title)
        Column(modifier = Modifier.padding(8.dp)) {
            ImageSlider(product.images)
            Text(text = "Details", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            CustomText("Category: ${product.category}")
            CustomText("Brand: ${product.brand}")
            CustomText("Name: ${product.title}")
            CustomText("Price: $${product.price}")
            CustomText("Product Description: ${product.description}")
            CustomText("Rating: ${product.rating} / 5")
            CustomText("Remaining: ${product.stock}")
        }
    }
}

@Composable
fun ImageSlider(images: List<String>) {
    LazyRow() {
        items(images) {
            SubcomposeAsyncImage(model = it, contentDescription = "", modifier = Modifier
                .padding(8.dp)
                .size(350.dp, 250.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
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
 * @param[mainViewModel] to collect data from api
 * @param[applicationContext] to show toast etc.
 * @param[paddingValues] apply padding  to content
 * @param[navController] for navigation between screens
 */
@Composable
fun ProductScreen(
    mainViewModel: MainViewModel,
    applicationContext: Context,
    paddingValues: PaddingValues,
    navController: NavHostController,
) {
    val devices = mainViewModel.devices.collectAsState(initial = UiState.Loading)
    when (val result = devices.value) {
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
            Toast.makeText(applicationContext, result.message, Toast.LENGTH_SHORT).show()
        }

        is UiState.Success -> {
            println("Under Success")
            ShowDeviceList(mainViewModel, paddingValues, navController)
        }
    }
}

@Composable
fun ShowDeviceList(
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues,
    navController: NavHostController,
) {
    val data = mainViewModel.deviceList.collectAsState()
    Log.d(TAG, "data $data")
    data.value?.let { deviceList ->
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = paddingValues) {
            deviceList.products?.let { prod ->
                items(prod) { product ->
                    DeviceCard(product, navController)
                }
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
                        navController.navigate(MainActivity.detailScreen + product.id)
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
            CustomText("Brand: ${product.brand}")
            CustomText("Title: ${product.title}")
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