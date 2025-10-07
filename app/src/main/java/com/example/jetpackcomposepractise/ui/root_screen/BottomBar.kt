package com.example.jetpackcomposepractise.ui.root_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposepractise.data.model.BottomNavItem

@Composable
fun BottomBar(
    navItems: List<BottomNavItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp)) // Defines the shape of the glass
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
        )
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp)),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedItemIndex == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(index, item.route) },
                    icon = {
                        Icon(
                            imageVector = item.icon, contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}



/**
 * A preview that demonstrates the BottomBar with the glassmorphism effect.
 */
@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    val navItems = listOf(BottomNavItem.Home, BottomNavItem.Cart, BottomNavItem.Favorite)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A1B9A), Color(0xFF1565C0)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                    tileMode = TileMode.Clamp
                )
            )
    ) {
        BottomBar(
            navItems = navItems,
            selectedItemIndex = 0,
            onItemSelected = { _, _ -> },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

