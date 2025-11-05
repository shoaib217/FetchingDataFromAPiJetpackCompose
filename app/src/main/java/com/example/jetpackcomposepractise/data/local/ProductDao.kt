package com.example.jetpackcomposepractise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.model.UserCartItem
import com.example.jetpackcomposepractise.data.model.UserFavoriteProduct
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

    @Query("SELECT productId FROM favorite_products")
    fun getAllFavoriteProducts(): Flow<List<Int>>

    @Query("SELECT * FROM user_cart_item")
    fun getUserCartItem(): Flow<List<UserCartItem>>

    /**
     * Retrieves the total number of products stored in the database.
     * @return The total count of products as an Int.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int



    /**
     * Adds an item to the cart. If the item already exists, its quantity is incremented.
     * If it's a new item, it's inserted with a quantity of 1.
     * This operation is performed within a transaction to ensure atomicity.
     */
    @Transaction
    suspend fun upsertToCart(productId: Int) {
        val currentItem = getUserCartItemById(productId)
        if (currentItem == null) {
            // Item does not exist, insert it with quantity 1
            insertCartItem(UserCartItem(productId = productId, cartCount = 1))
        } else {
            // Item exists, increment its quantity
            updateCartItem(currentItem.copy(cartCount = currentItem.cartCount + 1))
        }
    }

    // You will need these helper methods if they don't already exist:

    @Query("SELECT * FROM user_cart_item WHERE productId = :productId")
    suspend fun getUserCartItemById(productId: Int): UserCartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: UserCartItem)

    @Update
    suspend fun updateCartItem(cartItem: UserCartItem)



    @Transaction
    suspend fun removeFromCart(productId: Int) {
        val currentItem = getUserCartItemById(productId)
        if (currentItem != null) {
            if (currentItem.cartCount > 1) {
                updateCartItem(currentItem.copy(cartCount = currentItem.cartCount - 1))
            } else {
                clearCartItem(productId)
            }
        }

    }

    /**
     * Sets the cartCount of a specific product to 0, effectively removing all units of that item from the cart.
     * @param productId The ID of the product to clear from the cart.
     */
    @Query("Delete FROM user_cart_item WHERE productId = :productId")
    fun clearCartItem(productId: Int)



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProductToFavorites(userFavoriteProduct: UserFavoriteProduct)

    @Query("DELETE FROM favorite_products WHERE productId = :productId")
    fun removeProductFromFavorites(productId: Int)


}
