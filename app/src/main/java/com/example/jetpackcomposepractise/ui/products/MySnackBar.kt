package com.example.jetpackcomposepractise.ui.products

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.jetpackcomposepractise.TAG
import kotlinx.coroutines.launch


@Composable
fun MySnackBar(snackBarHostState: SnackbarHostState, message: String, random: Int) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(random) {
        scope.launch {
            val snackBarResult = snackBarHostState.showSnackbar(
                message = message, actionLabel = "close", duration = SnackbarDuration.Short
            )

            when (snackBarResult) {
                SnackbarResult.Dismissed -> {
                    Log.d(TAG, "Dismissed")
                }

                SnackbarResult.ActionPerformed -> {
                    Log.d(TAG, "ActionPerformed")
                }
            }
        }
    }
}