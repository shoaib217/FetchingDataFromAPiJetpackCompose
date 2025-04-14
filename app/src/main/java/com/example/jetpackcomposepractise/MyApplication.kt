package com.example.jetpackcomposepractise

import android.app.Application
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    // Set the maximum size of the memory cache as a percentage of available memory.
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(cacheDir, "image_cache").apply { mkdirs() })
                    .maxSizePercent(0.02)
                    .build()
            }
            .logger(DebugLogger())
            //.logger(DebugLogger())  // Optional: for debugging
            .build()
    }

    companion object {
        lateinit var imageLoader: ImageLoader
    }
}