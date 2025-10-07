package com.example.jetpackcomposepractise.ui.product_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposepractise.R

/**
 * A Composable that displays a quantity selector with "+" and "-" buttons
 * and the current count in the middle, adhering to Material Theme.
 *
 * @param currentCount The current quantity to display.
 * @param onIncrease The callback invoked when the "+" button is clicked.
 * @param onDecrease The callback invoked when the "-" button is clicked.
 * @param modifier Optional Modifier for this component.
 * @param maxCount The maximum allowed count (optional).
 * @param buttonSize The size of the IconButton.
 * @param buttonBorderStroke The border stroke for the buttons, defaults to theme's outline.
 * @param countTextStyle The style for the count text, defaults to theme's bodyLarge.
 */
@Composable
fun QuantitySelector(
    currentCount: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
    maxCount: Int? = null,
    buttonSize: Dp = 36.dp,
    // Default uses MaterialTheme.colorScheme.outline
    buttonBorderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    // Default uses MaterialTheme.typography.bodyLarge and MaterialTheme.colorScheme.onSurface (implicitly)
    countTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface // Explicitly setting, often same as default
    ),
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrease Button
        OutlinedIconButton(
            onClick = {
                onDecrease()
            },
            modifier = Modifier.size(buttonSize),
            shape = MaterialTheme.shapes.small, // Themed shape
            border = buttonBorderStroke,         // Themed border
        ) {
            Icon(
                painter = painterResource(R.drawable.minus), // Ensure this resource exists
                // For Material Icon alternative:
                // imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease quantity",
                tint = MaterialTheme.colorScheme.primary // Themed tint for enabled
            )
        }

        // Count Text
        Text(
            text = currentCount.toString(), style = countTextStyle, // Themed text style
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Increase Button
        OutlinedIconButton(
            onClick = {
                if (maxCount == null || currentCount < maxCount) {
                    onIncrease()
                }
            },
            modifier = Modifier.size(buttonSize),
            shape = MaterialTheme.shapes.small, // Themed shape
            border = buttonBorderStroke,         // Themed border
            enabled = maxCount == null || currentCount < maxCount
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increase quantity",
                tint = if (maxCount == null || currentCount < maxCount) {
                    MaterialTheme.colorScheme.primary // Themed tint for enabled
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Themed tint for disabled
                }
            )
        }
    }
}


@Preview(showBackground = true, name = "Quantity Selector - Default")
@Composable
fun QuantitySelectorPreview() {
    MaterialTheme {
        QuantitySelector(currentCount = 1, onIncrease = { }, onDecrease = { })
    }
}