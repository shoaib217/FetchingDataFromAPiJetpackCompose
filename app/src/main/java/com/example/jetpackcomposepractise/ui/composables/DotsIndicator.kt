package com.example.jetpackcomposepractise.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = Color.Blue,
    unselectedColor: Color = Color.Gray,
    dotSize: Dp = 8.dp,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalDots) { index ->
            val color = if (index == selectedIndex) selectedColor else unselectedColor

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
                    // Add padding to space out the dots
                    .padding(8.dp)
            )
        }
    }
}