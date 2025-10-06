package com.example.jetpackcomposepractise.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposepractise.TAG
import com.example.jetpackcomposepractise.data.model.ClickActions
import com.example.jetpackcomposepractise.data.model.Product
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class) // For Card and other Material 3 components
@Composable
fun ProductDetailScreen(
    product: Product,
    actions: ClickActions,
    snackBarHostState: SnackbarHostState, // Optional actions
) {
    val scope = rememberCoroutineScope()
    Log.d(TAG, "ProductDetailScreen for: ${product.title}")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Make the whole screen scrollable if content exceeds screen height
            .padding(bottom = 16.dp) // Padding at the bottom for scrollable content
    ) {
        // --- Image Section ---
        ImageSlider(
            images = product.images, modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        actions.onAddQuantityItem(product.id)
                        scope.launch {
                            snackBarHostState.showSnackbar("${product.title} added to cart")

                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = product.stock > 0 // Disable if out of stock
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(
                            ButtonDefaults.IconSize
                        )
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Add to Cart")
                }
                val favoriteTooltipState = rememberTooltipState()
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip { Text(if (!product.isFavorite) "Removed from favorites" else "Added to favorites") }
                    },
                    state = favoriteTooltipState
                ) {
                    OutlinedIconButton(
                        // Using OutlinedIconButton for a secondary action look
                        onClick = {
                            scope.launch {
                                actions.onToggleFavorite(product.id)
                                favoriteTooltipState.show()
                            }
                        },
                        // border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Default outline
                    ) {
                        if (product.isFavorite) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Favorite",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
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

            if (!product.brand.isNullOrEmpty() && !product.brand.equals(
                    "null", ignoreCase = true
                )
            ) {
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
