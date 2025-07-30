package com.sreimler.currencyconverter.core.domain.policy

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SyncPolicyTest {

    @Test
    fun givenTimeDifferenceJustAboveThreshold_whenCheckingIsDataStale_thenReturnsTrue() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - DEFAULT_STALE_WINDOW_MS - 1L

        val actual = SyncPolicy.isDataStale(lastUpdated, current, DEFAULT_STALE_WINDOW_MS)

        assertTrue(actual)
    }

    @Test
    fun givenTimeDifferenceEqualsThreshold_whenCheckingIsDataStale_thenReturnsFalse() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - DEFAULT_STALE_WINDOW_MS

        val actual = SyncPolicy.isDataStale(lastUpdated, current, DEFAULT_STALE_WINDOW_MS)

        assertFalse(actual)
    }

    @Test
    fun givenTimeDifferenceJustBelowThreshold_whenCheckingIsDataStale_thenReturns_False() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - DEFAULT_STALE_WINDOW_MS + 1L

        val actual = SyncPolicy.isDataStale(lastUpdated, current, DEFAULT_STALE_WINDOW_MS)

        assertFalse(actual)
    }

    @Test
    fun givenFutureTimestamp_whenCheckingIsDataStale_thenReturnsFalse() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - DEFAULT_STALE_WINDOW_MS
        val future = current + 100L

        val actual = SyncPolicy.isDataStale(lastUpdated, future, DEFAULT_STALE_WINDOW_MS)

        assertFalse(actual)
    }


    @Test
    fun givenTimeDifferenceJustAboveThreshold_whenCheckingIsRefreshAllowed_thenReturnsTrue() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - MIN_REFRESH_WINDOW_MS - 1L

        val actual = SyncPolicy.isRefreshAllowed(lastUpdated, current, MIN_REFRESH_WINDOW_MS)

        assertTrue(actual)
    }

    @Test
    fun givenTimeDifferenceEqualsThreshold_whenCheckingIsRefreshAllowed_thenReturnsFalse() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - MIN_REFRESH_WINDOW_MS

        val actual = SyncPolicy.isRefreshAllowed(lastUpdated, current, MIN_REFRESH_WINDOW_MS)

        assertFalse(actual)
    }

    @Test
    fun givenTimeDifferenceJustBelowThreshold_whenCheckingIsRefreshAllowed_thenReturns_False() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - MIN_REFRESH_WINDOW_MS + 1L

        val actual = SyncPolicy.isRefreshAllowed(lastUpdated, current, MIN_REFRESH_WINDOW_MS)

        assertFalse(actual)
    }

    @Test
    fun givenFutureTimestamp_whenCheckingIsRefreshAllowed_thenReturnsFalse() {
        val current = System.currentTimeMillis()
        val lastUpdated = current - MIN_REFRESH_WINDOW_MS
        val future = current + 100L

        val actual = SyncPolicy.isRefreshAllowed(lastUpdated, future, MIN_REFRESH_WINDOW_MS)

        assertFalse(actual)
    }
}
