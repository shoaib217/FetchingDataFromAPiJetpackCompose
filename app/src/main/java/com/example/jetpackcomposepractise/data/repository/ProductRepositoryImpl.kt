package com.example.jetpackcomposepractise.data.repository

import com.example.jetpackcomposepractise.data.local.ProductDao
import com.example.jetpackcomposepractise.data.model.NetworkResponse
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.remote.APIService
import com.example.jetpackcomposepractise.util.NetworkConnectivityHelper
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of the [ProductRepository] interface.
 *
 * This class is responsible for orchestrating data operations for products. It manages fetching
 * data from either a remote API or a local database (caching), and handles all product-related
 * actions like adding to a cart, marking as favorite, etc. It acts as the single source of truth
 * for product data for the rest of the application.
 *
 * @property apiService The remote data source for fetching products.
 * @property productDao The local data source (Room DAO) for caching and retrieving products.
 * @property networkConnectivityHelper A utility to check the device's network status.
 */
class ProductRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val productDao: ProductDao, // Inject the DAO
    private val networkConnectivityHelper: NetworkConnectivityHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }

    override suspend fun syncProducts(): NetworkResponse {
        val isDbEmpty = productDao.getProductCount() == 0
        if (!isDbEmpty) {
            return NetworkResponse.Success // DB is already populated, no action needed
        }

        // If the DB is empty, try fetching from the network
        if (networkConnectivityHelper.isNetworkAvailable()) {
            return try {
                val response = apiService.getAllProducts()
                if (response.isSuccessful) {
                    val products = response.body()?.products
                    if (!products.isNullOrEmpty()) {
                        productDao.insertAll(products) // Save to database
                        NetworkResponse.Success
                    } else {
                        NetworkResponse.Error("No products found from API")
                    }
                } else {
                    NetworkResponse.Error(response.message())
                }
            } catch (e: Exception) {
                NetworkResponse.Error("Failed to fetch from network: ${e.message}")
            }
        } else {
            // DB is empty and there's no network
            return NetworkResponse.Error("No internet connection and no data in cache.")
        }
    }

    override suspend fun addToCart(productId: Int) {
        withContext(ioDispatcher) {
            productDao.addToCart(productId)
        }
    }

    override suspend fun removeFromCart(productId: Int) {
        withContext(ioDispatcher) {
            productDao.removeFromCart(productId)
        }
    }

    override suspend fun clearCartItem(productId: Int) {
        withContext(ioDispatcher) {
            productDao.clearCartItem(productId)
        }
    }

    override suspend fun markProductFavorite(productId: Int, isFavorite: Boolean) {
        withContext(ioDispatcher) {
            productDao.markProductFavorite(productId, isFavorite)
        }
    }
}