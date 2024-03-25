package com.sreimler.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sreimler.currencyconverter.ui.screens.ConverterScreen
import com.sreimler.currencyconverter.ui.screens.InfoScreen
import com.sreimler.currencyconverter.ui.screens.ListScreen
import com.sreimler.currencyconverter.ui.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.viewmodel.CurrencyConverterViewModel

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

enum class Screen(@StringRes val title: Int) {
    List(title = R.string.currency_list),
    Converter(title = R.string.app_name),
    Info(title = R.string.info)
}

@Composable
fun CurrencyConversionApp() {
    val viewModel: CurrencyConverterViewModel = viewModel()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(backStackEntry?.destination?.route ?: Screen.List.name)

    Scaffold(
        topBar = {
            CurrencyConverterTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                openInfo = { navController.navigate(Screen.Info.name) }
            )
        },
        floatingActionButton = {
            if (currentScreen.name == Screen.List.name) {
                ConverterFab(onClick = {
                    navController.navigate(Screen.Converter.name)
                })
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.List.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp)
        ) {
            composable(route = Screen.List.name) {
                val uiState = viewModel.currencyUiState.collectAsState()
                ListScreen(uiState = uiState.value)
            }
            composable(route = Screen.Converter.name) {
                val uiState = viewModel.currencyUiState.collectAsState()
                ConverterScreen(
                    uiState = uiState.value,
                    onChange = { amount, currency -> viewModel.amountChanged(amount, currency) })
            }
            composable(route = Screen.Info.name) {
                InfoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterTopBar(
    currentScreen: Screen, canNavigateBack: Boolean, navigateUp: () -> Unit, openInfo: () ->
    Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(currentScreen.title), fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            if (currentScreen != Screen.Info) {
                IconButton(onClick = openInfo) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                }
            }
        }
    )
}

@Composable
fun ConverterFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.compare_arrow_24),
                contentDescription = stringResource(id = R.string.currency_conversion)
            )
        },
        text = { Text("Convert") },
        onClick = { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CurrencyConverterTheme {
        CurrencyConversionApp()
    }
}