package com.example.jetpackcomposepractise.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepractise.data.model.Filter
import com.example.jetpackcomposepractise.data.model.NetworkResponse
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.model.UiState
import com.example.jetpackcomposepractise.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    // A private mutable state flow to hold the master list of all products
    // This is the single source of truth that will never be filtered or sorted directly.
    private val _products = productRepository.getProducts().map {
        performRupeesConversion(it)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()// Provide an appropriate initial value
        )

    // State flows to hold the current filter and sort selections
    private val _currentFilter = MutableStateFlow<Filter?>(null)
    private val _currentCategory = MutableStateFlow<String?>("All")

    // Public state flow for the UI state (loading, success, error)
    private var _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Public state flow for the list of categories, derived from the master product list
    val categoryList: StateFlow<List<String>?> = _products.map { products ->
        products.groupBy { it.category }.keys.toMutableList().apply {
            add(0, "All")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val favoriteDevice: StateFlow<List<Product>> =
        combine(productRepository.getFavoriteProducts(), _products) { favoriteIds, productList ->
            productList.filter { it.id in favoriteIds }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L), // Use underscore for readability
            initialValue = emptyList() // Use a non-nullable type with a sensible default
        )

    val cartItems: StateFlow<List<Product>> =
        combine(
            productRepository.getUserCartItem(),
            _products
        ) { userCartItems, productList ->
            // For efficient lookups, convert the cart items list to a map where the key is the product ID.
            val cartItemMap = userCartItems.associateBy { it.productId }

            // Use map to create a new list with updated cart counts, promoting immutability.
            productList.map { product ->
                val cartCount = cartItemMap[product.id]?.cartCount ?: 0
                // Use the copy method (assuming Product is a data class) to create an updated object.
                product.copy(cartCount = cartCount)
            }.filter {
                it.cartCount > 0
            }
        }.stateIn(
            scope = viewModelScope,
            // Keep the flow active for 5s after the last collector stops, useful for screen rotations.
            started = SharingStarted.WhileSubscribed(5_000L),
            // Provide an empty list as the initial state before the flows emit their first values.
            initialValue = emptyList()
        )


    val deviceList: StateFlow<List<Product>> = combine(
        _products,
        _currentCategory,
        _currentFilter,
        favoriteDevice,
        cartItems,
    ) { products, category, filter, favoriteProducts, cartItems ->
        // 1. Pre-process for efficient lookups (O(1))
        val favoriteProductIds = favoriteProducts.map { it.id }.toSet()
        val cartItemMap = cartItems.associateBy({ it.id }, { it.cartCount })

        products.asSequence()
            // 2. Apply category filter
            .filter { product ->
                category.equals("All", ignoreCase = true) || product.category.equals(category, ignoreCase = true)
            }
            // 3. Apply price-based filter
            .filter { product ->
                when (filter) {
                    Filter.BETWEEN_500_1000 -> product.price in 500.0..1000.0
                    Filter.OVER_1000 -> product.price > 1000
                    Filter.UNDER_500 -> product.price < 500
                    Filter.OVER_5000 -> product.price > 5000
                    Filter.OVER_10000 -> product.price > 10000
                    else -> true // Keep item if it's a sort filter or null
                }
            }
            // 4. Map to new, immutable objects with updated state
            .map { product ->
                product.copy(
                    isFavorite = product.id in favoriteProductIds,
                    cartCount = cartItemMap[product.id] ?: 0
                )
            }
            // 5. Apply sorting if required, converting sequence to a sorted list
            .let { sequence ->
                when (filter) {
                    Filter.RATING -> sequence.sortedByDescending { it.rating }.toList()
                    Filter.PRICE_ASC -> sequence.sortedBy { it.price }.toList()
                    Filter.PRICE_DESC -> sequence.sortedByDescending { it.price }.toList()
                    Filter.NAME -> sequence.sortedBy { it.title }.toList()
                    // If not sorting, convert the final sequence to a list
                    else -> sequence.toList()
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList() // 6. Use an empty list for the initial state
    )




    init {
        getDevices()
    }

    private fun getDevices() {
        Log.d("viemodel", "getDevices")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                when (val responseData = productRepository.syncProducts()) {
                    is NetworkResponse.Error -> {
                        _uiState.value = UiState.Error(responseData.message)
                    }

                    is NetworkResponse.Success -> {
                        // The master list is updated only once on a successful API call.
                        _uiState.value = UiState.Success
                    }
                }
            } catch (e: Exception) {
                Log.e("Exception", e.message ?: "Something went wrong")
                _uiState.value = UiState.Error("Something went wrong")
            }
        }
    }

    private fun performRupeesConversion(product: List<Product>): List<Product> {
        return product.map {
            it.copy(price = it.price.convertToRupees())
        }
    }

    private fun Double.convertToRupees(): Double {
        val conversionRate = 88.09
        return this * conversionRate
    }

    // Now, these functions simply update the state of the filter/category.
    // The `deviceList` flow automatically handles the re-filtering and re-sorting.
    fun filterProductByCategory(category: String) {
        _currentCategory.value = category
    }

    fun setFilterData(filter: Filter?) {
        _currentFilter.value = filter
    }

    // These functions now update the master list (`_products`).
    // All other derived flows automatically react to these changes.
    fun markProductAsFavorite(productId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                productRepository.addProductToFavorites(productId)
            } else {
                productRepository.removeProductFromFavorites(productId)
            }
        }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            productRepository.addToCart(productId)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            productRepository.removeFromCart(productId)
        }
    }

    fun clearCartItem(productId: Int) {
        viewModelScope.launch {
            productRepository.clearCartItem(productId)
        }
    }
}

