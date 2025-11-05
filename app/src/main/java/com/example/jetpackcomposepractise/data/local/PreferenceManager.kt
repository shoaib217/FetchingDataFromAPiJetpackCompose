    package com.example.jetpackcomposepractise.data.local

    import android.content.Context
    import dagger.hilt.android.qualifiers.ApplicationContext
    import javax.inject.Inject
    import javax.inject.Singleton

    @Singleton
    class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {

        private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        companion object {
            private const val KEY_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
            // Set the cache duration. For 3 hours, it's 3 * 60 * 60 * 1000 milliseconds.
            const val CACHE_DURATION_MS = 3 * 60 * 60 * 1000L
        }

        /**
         * Saves the current time as the last successful sync timestamp.
         */
        fun saveSyncTimestamp() {
            sharedPreferences.edit().putLong(KEY_LAST_SYNC_TIMESTAMP, System.currentTimeMillis()).apply()
        }

        /**
         * Checks if the cache is still valid based on the CACHE_DURATION_MS.
         * @return `true` if the cache is stale and a new sync is needed, `false` otherwise.
         */
        fun isCacheStale(): Boolean {
            val lastSync = sharedPreferences.getLong(KEY_LAST_SYNC_TIMESTAMP, 0)
            // If it was never synced (lastSync is 0), it's definitely stale.
            if (lastSync == 0L) return true

            val currentTime = System.currentTimeMillis()
            return (currentTime - lastSync) > CACHE_DURATION_MS
        }
    }
    