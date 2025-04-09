package com.sreimler.currencyconverter.core.domain.policy

object SyncPolicy {
    fun isDataStale(
        lastUpdated: Long,
        staleAfterMillis: Long = DEFAULT_STALE_WINDOW_MS
    ): Boolean {
        return System.currentTimeMillis() - lastUpdated > staleAfterMillis
    }

    fun isRefreshAllowed(
        lastUpdated: Long,
        allowedAfterMillis: Long = MIN_REFRESH_WINDOW_MS
    ): Boolean {
        return System.currentTimeMillis() - lastUpdated > allowedAfterMillis
    }

    private const val DEFAULT_STALE_WINDOW_MS: Long = 24 * 60 * 60 * 1000 // 24 hours
    private const val MIN_REFRESH_WINDOW_MS: Long = 1 * 60 * 60 * 1000 // 1 hour
//    private const val MIN_REFRESH_WINDOW_MS: Long = 1 * 1 * 60 * 1000 // 1 minute
}