package com.example.jetpackcomposepractise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jetpackcomposepractise.data.model.Product
import com.example.jetpackcomposepractise.data.model.UserCartItem
import com.example.jetpackcomposepractise.data.model.UserFavoriteProduct

@Database(entities = [Product::class, UserFavoriteProduct::class, UserCartItem::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
