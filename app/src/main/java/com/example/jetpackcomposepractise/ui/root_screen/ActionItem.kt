package com.example.jetpackcomposepractise.ui.root_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.jetpackcomposepractise.data.model.ClickActions


@Composable
fun ActionItems(showBottomBar: Boolean, categoryList: List<String>?,clickActions: ClickActions) {
    var displayMenuAppbar by remember {
        mutableStateOf(false)
    }

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
    AnimatedVisibility(
        displayMenuAppbar,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400))
    ) {
        DropdownMenu(
            expanded = displayMenuAppbar, onDismissRequest = {
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