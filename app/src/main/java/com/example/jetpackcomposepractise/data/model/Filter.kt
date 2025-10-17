package com.example.jetpackcomposepractise.data.model

enum class Filter(val value: String) {
    PRICE_ASC("Price: Low to High"),
    PRICE_DESC("Price: High to Low"),
    RATING("Customer Rating"),
    NAME("Name (A-Z)"),
    UNDER_500("₹0 - ₹500"),
    BETWEEN_500_1000("₹500 - ₹1000"),
    OVER_1000("₹1000+"),
    OVER_5000("₹5000+"),
    OVER_10000("₹10000+")
}