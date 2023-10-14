package com.example.jetpackcomposepractise

import android.util.Log
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepractise.data.APIService
import com.example.jetpackcomposepractise.data.DeviceList
import com.example.jetpackcomposepractise.data.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var _devices = MutableStateFlow<UiState>(UiState.Loading)
    val devices: StateFlow<UiState> = _devices
    private var _deviceList = MutableStateFlow<DeviceList?>(null)
    val deviceList: StateFlow<DeviceList?> = _deviceList
    private var _categoryList = MutableStateFlow<Map<String, List<Product>>?>(null)
    val categoryList: StateFlow<Map<String, List<Product>>?> = _categoryList
    private var categoryDeviceList: DeviceList? = null

    init {
        getDevices()
    }

    private fun getDevices() {
        Log.d("viemodel","getDevices")
        val apiService = APIService.getInstance()
        viewModelScope.launch {
            try {
                delay(600)
                val product = apiService.getAllProducts()
                _deviceList.value = product
                categoryDeviceList = product
                _categoryList.value = product?.products?.groupBy { it.category }
                _devices.emit(UiState.Success)
            } catch (e: Exception) {
                Log.e("Exception", e.message ?: "Something went wrong")
                _devices.emit(UiState.Error("Something went wrong"))
            }
        }
    }

    fun getProduct(id: Int?): Product? {
        return deviceList.value?.products?.first {
            it.id == id
        }
    }

    fun setFilterData(category: String) {
        viewModelScope.launch {
            _deviceList.value = categoryDeviceList
            _deviceList.emit(
                if (category.equals("all", true)) categoryDeviceList else
                    _deviceList.value?.copy(products = _deviceList.value?.products?.filter { it.category == category })
            )
        }
    }
}

sealed class UiState {
    object Success : UiState()
    class Error(val message: String) : UiState()
    object Loading : UiState()
}