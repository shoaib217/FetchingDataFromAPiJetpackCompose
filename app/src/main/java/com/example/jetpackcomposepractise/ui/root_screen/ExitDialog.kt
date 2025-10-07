package com.example.jetpackcomposepractise.ui.root_screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ExitDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit, // Renamed from onYes for clarity, standard practice
    // Optional parameters for more flexibility
    dialogTitle: String = "Alert", // Consider using stringResource(R.string.alert_dialog_title)
    dialogText: String = "Are you sure you want to exit?", // Consider using stringResource(R.string.exit_dialog_message)
    confirmButtonText: String = "Yes", // Consider using stringResource(R.string.yes)
    dismissButtonText: String = "No", // Consider using stringResource(R.string.no)
    icon: ImageVector? = null, // Optional: For adding an icon to the dialog
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.headlineSmall, // Use a theme typography style
                color = MaterialTheme.colorScheme.onSurface // Color for text on the dialog's surface
            )
        },
        text = {
            Text(
                text = dialogText,
                style = MaterialTheme.typography.bodyMedium, // Use a theme typography style
                color = MaterialTheme.colorScheme.onSurfaceVariant // Slightly less prominent color for body
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(
                    text = confirmButtonText,
                    // TextButton text color defaults to MaterialTheme.colorScheme.primary, which is usually correct.
                    // You could explicitly set it: color = MaterialTheme.colorScheme.primary
                    fontWeight = FontWeight.SemiBold // Optional: to make action text slightly bolder
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissButtonText
                    // color = MaterialTheme.colorScheme.primary
                )
            }
        },
        // You can also theme the dialog container itself if needed,
        // though defaults are usually good.
        // containerColor = MaterialTheme.colorScheme.surface,
        // titleContentColor = MaterialTheme.colorScheme.onSurface,
        // textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}