package com.sreimler.currencyconverter

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.WindowMetricsCalculator
import com.sreimler.currencyconverter.core.presentation.CurrencyConversionApp
import com.sreimler.currencyconverter.navigation.MainNavHost
import timber.log.Timber


/**
 * MainActivity serves as the entry point of the application.
 * It sets up the UI using Jetpack Compose and handles orientation restrictions based on the device's screen size.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add a container to allow detecting/handling configuration changes
        val container = FrameLayout(this)
        setContentView(container)

        // Check if device orientation should be restricted
        requestedOrientation = customizeOrientationSetting()

        container.addView(object : View(this) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)

                Timber.i("Configuration change detected. Device is${if (!isCompactScreen()) " not" else ""} compact")
                requestedOrientation = customizeOrientationSetting()
            }
        })

        val composeView = ComposeView(this).apply {
            setContent {
                val navController = rememberNavController()
                CurrencyConversionApp(navController) {
                    MainNavHost(navController = navController)
                }
            }
        }
        container.addView(composeView)
    }

    /**
     * Determines the appropriate orientation setting based on the device's screen size.
     *
     * @return The orientation setting: portrait for compact screens, unspecified otherwise.
     */
    private fun customizeOrientationSetting(): Int {
        return if (isCompactScreen()) {
            Timber.i("Restricting orientation to portrait")
            SCREEN_ORIENTATION_PORTRAIT
        } else {
            Timber.i("Not restricting orientation")
            SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    /**
     * Determines whether the device has a compact screen.
     *
     * @return True if the screen width is less than 600dp, false otherwise.
     */
    private fun isCompactScreen(): Boolean {
        val metrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(this)
        val widthDp = metrics.bounds.width() / resources.displayMetrics.density
        val isCompact = widthDp < 600 // 600dp is the Material 3 compact breakpoint
        return isCompact
    }
}
