package com.example.jetpackcomposepractise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposepractise.data.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the product table.
 * Provides the app with methods to query, update, insert, and delete data in the
 * `products` table.
 */
@Dao
interface ProductDao {

    /**
     * Inserts a list of products into the database.
     * If a product with the same primary key already exists, it will be replaced.
     * This is a suspend function and should be called from a coroutine scope.
     *
     * @param products The list of [Product] entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    /**
     * Retrieves all products from the database as a Flow.
     *
     * This function observes the 'products' table for any changes and automatically
     * emits a new list of products whenever the data is updated.
     *
     * @return A Flow that emits a list of all [Product] objects.
     */
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    /**
     * Retrieves the total number of products stored in the database.
     * @return The total count of products as an Int.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int


    /**
     * Increments the cartQuantity of a product by 1, based on its ID.
     * @param productId The ID of the product to update.
     */
    @Query("UPDATE products SET cartCount = cartCount + 1 WHERE id = :productId")
    fun addToCart(productId: Int)

    /**
     * Decrement the cartQuantity of a product by 1, based on its ID.
     * @param productId The ID of the product to update.
     */
    @Query("UPDATE products SET cartCount = cartCount - 1 WHERE id = :productId")
    fun removeFromCart(productId: Int)

    /**
     * Sets the cartCount of a specific product to 0, effectively removing all units of that item from the cart.
     * @param productId The ID of the product to clear from the cart.
     */
    @Query("UPDATE products SET cartCount = 0 WHERE id = :productId")
    fun clearCartItem(productId: Int)


    /**
     * Updates the favorite status of a product.
     * @param productId The ID of the product to update.
     * @param isFavorite The new favorite status to set.
     */
    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :productId")
    fun markProductFavorite(productId: Int, isFavorite: Boolean)


}
