package com.example.jetpackcomposepractise.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepractise.Filter
import com.example.jetpackcomposepractise.data.model.NetworkResponse
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private var _devices = MutableStateFlow<UiState>(UiState.Loading)
    val devices: StateFlow<UiState> = _devices
    private var _deviceList = MutableStateFlow<List<Product>?>(null)
    val deviceList: StateFlow<List<Product>?> = _deviceList

    // Correct way to derive favoriteDevice StateFlow
    val favoriteDevice: StateFlow<List<Product>?> = deviceList.map { list ->
        list?.filter { it.isFavorite }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Or Lazily, Eagerly
        initialValue = null // Or derive from current _deviceList.value if needed
    )


    val cartItems: StateFlow<List<Product>?> = deviceList.map { list ->
        list?.filter { it.cartCount > 0}
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Or Lazily, Eagerly
        initialValue = null // Or derive from current _deviceList.value if needed
    )

    private var _categoryList = MutableStateFlow<ArrayList<String>?>(null)
    val categoryList: StateFlow<ArrayList<String>?> = _categoryList
    private var categoryDeviceList: List<Product>? = null

    val isRefreshing = MutableStateFlow(false)

    init {
        getDevices()
    }

    private fun getDevices() {
        Log.d("viemodel", "getDevices")
        viewModelScope.launch {
            try {
                val responseData = productRepository.getProducts()
                delay(500)
                when (responseData) {
                    is NetworkResponse.Error -> {
                        _devices.emit(UiState.Error(responseData.message))
                        isRefreshing.value = false

                    }

                    is NetworkResponse.Success -> {
                        val indianCurrencyConversionList = performRupeesConversion(responseData.productList)
                        _deviceList.value = indianCurrencyConversionList
                        categoryDeviceList = indianCurrencyConversionList
                        _categoryList.value =
                            indianCurrencyConversionList.groupBy { it.category }.keys.toCollection(
                                arrayListOf("All")
                            )
                        isRefreshing.value = false
                        _devices.emit(UiState.Success)
                    }
                }

            } catch (e: Exception) {
                Log.e("Exception", e.message ?: "Something went wrong")
                _devices.emit(UiState.Error("Something went wrong"))
                isRefreshing.value = false

            }
        }
    }

    fun performRupeesConversion(product: List<Product>): List<Product> {
        return product.map {
            it.copy(price = it.price.convertToRupees())
        }
    }

    fun Double.convertToRupees(): Double {
        val  conversionRate = 88.09
        return this * conversionRate
    }
    fun filterProductByCategory(category: String) {
        viewModelScope.launch {
            _deviceList.value = categoryDeviceList
            _deviceList.emit(
                if (category.equals("all", true)) categoryDeviceList else
                    deviceList.value?.filter { it.category == category }
            )

        }
    }

    fun setFilterData(filter: Filter?) {
        when (filter) {
            Filter.PRICE -> {
                _deviceList.value = _deviceList.value?.sortedByDescending { it.price }
            }

            Filter.RATING -> {
                _deviceList.value =
                    _deviceList.value?.sortedByDescending { it.rating }
            }

            Filter.STOCK -> {
                _deviceList.value =
                    _deviceList.value?.sortedByDescending { it.stock }
            }

            null -> {
                _deviceList.value = categoryDeviceList
            }
        }

    }

    fun markProductAsFavorite(productId: Int) {
        val currentList = _deviceList.value ?: return // If list is null, do nothing

        val updatedList = currentList.map { product ->
            if (product.id == productId) {
                // Create a new product instance with the toggled favorite status
                product.copy(isFavorite = !product.isFavorite)
            } else {
                product // Return the same product instance if it's not the one we're looking for
            }
        }
        _deviceList.value = updatedList
    }

    fun addToCart(productId: Int) {
        val currentList = _deviceList.value ?: return // If list is null, do nothing

        val updatedList = currentList.map { product ->
            if (product.id == productId) {
                // Create a new product instance with the toggled favorite status
                product.copy(cartCount = product.cartCount + 1)
            } else {
                product // Return the same product instance if it's not the one we're looking for
            }
        }
        _deviceList.value = updatedList
    }

    fun removeFromCart(productId: Int) {
        val currentList = _deviceList.value ?: return // If list is null, do nothing

        val updatedList = currentList.map { product ->
            if (product.id == productId) {
                // Create a new product instance with the toggled favorite status
                product.copy(cartCount = product.cartCount - 1)
            } else {
                product // Return the same product instance if it's not the one we're looking for
            }
        }
        _deviceList.value = updatedList
    }
}

sealed class UiState {
    object Success : UiState()
    class Error(val message: String) : UiState()
    object Loading : UiState()
}