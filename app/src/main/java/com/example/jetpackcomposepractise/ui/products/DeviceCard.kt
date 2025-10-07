package com.example.jetpackcomposepractise.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.jetpackcomposepractise.MainActivity.Companion.DETAIL_SCREEN
import com.example.jetpackcomposepractise.data.model.Product
import java.util.Locale

@Composable
fun DeviceCard(product: Product, navController: NavHostController) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
        defaultElevation = 6.dp // Slightly reduced elevation can look cleaner
    ), colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ), shape = RoundedCornerShape(12.dp), // Consistent corner rounding
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp, vertical = 6.dp
            ) // Padding around the card itself in the grid
            .clickable(
                onClick = {
                    navController.navigate(DETAIL_SCREEN + product.id)
                })
            .wrapContentHeight() // Allow card to grow based on content
    ) {
        // Explicit Column to arrange content within the card
        Column(
            modifier = Modifier.padding(12.dp) // Inner padding for the content
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
                        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
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
                value = "₹${
                    String.format(
                        Locale.ENGLISH, "%.2f", product.price
                    )
                }", // Just the price for cleaner look
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
