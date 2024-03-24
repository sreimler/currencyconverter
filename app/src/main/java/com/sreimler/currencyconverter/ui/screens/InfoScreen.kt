package com.sreimler.currencyconverter.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val PRIVACY_POLICY_URL = "https://www.iubenda.com/privacy-policy/68707157"

@Composable
fun InfoScreen(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = false
                    webViewClient = WebViewClient()

                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                }
            },
            update = { webView ->
                webView.loadUrl(PRIVACY_POLICY_URL)
            }
        )
    }
}
