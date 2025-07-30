package com.sreimler.currencyconverter.core.domain.policy


internal const val DEFAULT_STALE_WINDOW_MS: Long = 24 * 60 * 60 * 1000 // 24 hours
internal const val MIN_REFRESH_WINDOW_MS: Long = 1 * 60 * 60 * 1000 // 1 hour
//internal const val MIN_REFRESH_WINDOW_MS: Long = 1 * 1 * 60 * 1000 // 1 minute

object SyncPolicy {
    fun isDataStale(
        lastUpdated: Long,
        current: Long = System.currentTimeMillis(),
        staleAfterMillis: Long = DEFAULT_STALE_WINDOW_MS
    ): Boolean {
        if (current > System.currentTimeMillis()) return false // return false if a future timestamp has been passed
        return current - lastUpdated > staleAfterMillis
    }

    fun isRefreshAllowed(
        lastUpdated: Long,
        current: Long = System.currentTimeMillis(),
        allowedAfterMillis: Long = MIN_REFRESH_WINDOW_MS
    ): Boolean {
        if (current > System.currentTimeMillis()) return false // return false if a future timestamp has been passed
        return current - lastUpdated > allowedAfterMillis
    }
}
