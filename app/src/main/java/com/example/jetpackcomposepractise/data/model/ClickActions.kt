package com.example.jetpackcomposepractise.data.model

interface ClickActions {
    fun filterProductByCategory(category: String)

    fun filterProductByType(selectedFilter: Filter?)

    fun onDismiss()

    fun onAddQuantityItem(productId: Int)
    fun onToggleFavorite(productId: Int, isFavorite: Boolean)
    fun onRemoveQuantityItem(productId: Int)
    fun clearCartItem(productId: Int)

}