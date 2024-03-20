package com.sreimler.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sreimler.currencyconverter.ui.screens.ListScreen
import com.sreimler.currencyconverter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CurrencyConversionApp()
                }
            }
        }
    }
}

enum class CurrencyConverterScreen(@StringRes val title: Int) {
    List(title = R.string.currency_list),
    Converter(title = R.string.app_name)
}

@Composable
fun CurrencyConversionApp() {
    // TODO: create viewmodel
    // TODO: set up nav controller and host
    val currentScreen = CurrencyConverterScreen.List
    Scaffold(
        topBar = { CurrencyConverterTopBar(currentScreen) },
        floatingActionButton = {
            if (currentScreen.name == CurrencyConverterScreen.List.name) ConverterFab(onClick = {})
        }
    ) {
        // TODO: define routes for navhost
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp)
        ) {
            ListScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterTopBar(currentScreen: CurrencyConverterScreen) {
    TopAppBar(title = { Text(text = stringResource(currentScreen.title)) })
}

@Composable
fun ConverterFab(onClick: () -> Unit) {
    LargeFloatingActionButton(
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.compare_arrow), contentDescription = stringResource
                (id = R.string.currency_conversion)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CurrencyConverterTheme {
        CurrencyConversionApp()
    }
}