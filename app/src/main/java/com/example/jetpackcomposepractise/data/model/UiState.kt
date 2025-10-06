package com.example.jetpackcomposepractise.data.model

sealed class UiState {
    object Success : UiState()
    class Error(val message: String) : UiState()
    object Loading : UiState()
}
