package com.example.jetpackcomposepractise.ui.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.jetpackcomposepractise.data.model.ClickActions
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.ui.product_detail.QuantitySelector
import com.example.jetpackcomposepractise.ui.shared_widget.PlaceholderScreen
import java.util.Locale

@Composable
fun CartScreen(
    cartItems: List<Product>?,
    clickActions: ClickActions,
    navigateToDetailScreen: (id: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cartItems.isNullOrEmpty()) {
            PlaceholderScreen(screenName = "Your Cart is Empty...")
        } else {
            // Header
            Text(
                text = "Shopping Cart",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Cart Items List
            LazyColumn(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { product ->
                    CartItemCard(product, clickActions, navigateToDetailScreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Price Card
            val totalPrice = cartItems.sumOf { it.price * it.cartCount }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total:", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%.2f", totalPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Checkout Button
            Button(
                onClick = { /* Handle checkout logic */ }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Checkout")
            }
        }
    }
}

@Composable
fun CartItemCard(
    product: Product,
    actions: ClickActions,
    navigateToDetailScreen: (id: Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            navigateToDetailScreen.invoke(product.id)
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // This disables the ripple effect
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Product Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    SubcomposeAsyncImage(
                        model = product.images.firstOrNull(),
                        contentDescription = product.title,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.title ?: "No Title",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "₹${
                                String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    product.price
                                )
                            }",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Delete Button
                IconButton(onClick = { actions.clearCartItem(product.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from cart",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantitySelector(
                    currentCount = product.cartCount,
                    onIncrease = { actions.onAddQuantityItem(product.id) },
                    onDecrease = { actions.onRemoveQuantityItem(product.id) },
                    maxCount = product.stock,
                )
                val itemSubtotal = product.price * product.cartCount
                Text(
                    text = "Subtotal: ₹${String.format(Locale.getDefault(), "%.2f", itemSubtotal)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}


