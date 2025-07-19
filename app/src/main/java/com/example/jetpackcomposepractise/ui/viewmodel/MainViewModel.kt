package com.example.jetpackcomposepractise.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepractise.Filter
import com.example.jetpackcomposepractise.data.model.DeviceList
import com.example.jetpackcomposepractise.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private var _devices = MutableStateFlow<UiState>(UiState.Loading)
    val devices: StateFlow<UiState> = _devices
    private var _deviceList = MutableStateFlow<DeviceList?>(null)
    val deviceList: StateFlow<DeviceList?> = _deviceList
    private var _categoryList = MutableStateFlow<ArrayList<String>?>(null)
    val categoryList: StateFlow<ArrayList<String>?> = _categoryList
    private var categoryDeviceList: DeviceList? = null

    init {
        getDevices()
    }

    private fun getDevices() {
        Log.d("viemodel","getDevices")
        viewModelScope.launch {
            try {
                val product = productRepository.getProducts()
                _deviceList.value = product
                categoryDeviceList = product
                _categoryList.value =  product?.products?.groupBy { it.category }?.keys?.toCollection(
                    arrayListOf("All")
                )
                delay(300)
                _devices.emit(UiState.Success)
            } catch (e: Exception) {
                Log.e("Exception", e.message ?: "Something went wrong")
                _devices.emit(UiState.Error("Something went wrong"))
            }
        }
    }

    fun filterProductByCategory(category: String) {
        viewModelScope.launch {
            _deviceList.value = categoryDeviceList
            _deviceList.emit(
                if (category.equals("all", true)) categoryDeviceList else
                    _deviceList.value?.copy(products = _deviceList.value?.products?.filter { it.category == category })
            )
        }
    }

    fun setFilterData(filter: Filter?) {
        when(filter){
            Filter.PRICE -> {
                _deviceList.value = _deviceList.value?.copy(products = _deviceList.value?.products?.sortedByDescending { it.price })
            }
            Filter.RATING -> {
                _deviceList.value = _deviceList.value?.copy(products = _deviceList.value?.products?.sortedByDescending { it.rating})
            }
            Filter.STOCK -> {
                _deviceList.value = _deviceList.value?.copy(products = _deviceList.value?.products?.sortedByDescending { it.stock})
            }
            null -> {
                _deviceList.value = categoryDeviceList
            }
        }

    }
}

sealed class UiState {
    object Success : UiState()
    class Error(val message: String) : UiState()
    object Loading : UiState()
}