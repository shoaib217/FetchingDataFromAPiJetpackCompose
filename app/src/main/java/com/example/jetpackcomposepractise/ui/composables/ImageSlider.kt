package com.example.jetpackcomposepractise.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSlider(images: List<String>, modifier: Modifier = Modifier) {

    // 1. Remember the state of the LazyRow
    val lazyListState = rememberLazyListState()

    // 2. Calculate the currently visible item index (the "page")
    val currentPage = remember {
        derivedStateOf {
            // Gets the index of the first completely visible item
            lazyListState.firstVisibleItemIndex
        }
    }

    // 3. Stack the slider and the dots vertically
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        LazyRow(
            // Use SnapFlingBehavior to ensure the row stops exactly on item boundaries
            // which makes the "paging" feel clean and ensures currentPage is accurate.
            flingBehavior = rememberSnapFlingBehavior(lazyListState), state = lazyListState,
            // Ensure content fills the entire space horizontally for full-screen slides
            modifier = Modifier.fillMaxWidth()
        ) {
            items(images) { url ->
                SubcomposeAsyncImage(
                    model = url,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .fillParentMaxWidth() // Important: Make each item take full width for clean paging
                        .aspectRatio(1f) // Maintain aspect ratio if needed, or use .fillMaxWidth().height(300.dp)
                        .padding(horizontal = 0.dp), // Remove horizontal padding here for clean snapping
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        }
                    })
            }
        }

        // Add vertical spacing between the slider and the dots
        Spacer(modifier = Modifier.height(12.dp))

        // 4. Place the Dots Indicator below the slider
        DotsIndicator(
            totalDots = images.size,
            selectedIndex = currentPage.value,
            selectedColor = Color(0xFF007AFF) // A nice active blue
        )
    }
}
