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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sreimler.currencyconverter.converter.presentation.ConverterScreenRoot
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme
import com.sreimler.currencyconverter.list.presentation.CurrencyListScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // TODO: add splash screen
                    // TODO: handle loading
                    // TODO: display errors, if any
                    // TODO: implement swipe to refresh
                    CurrencyConversionApp()
                }
            }
        }
    }
}

enum class Screen(@StringRes val title: Int) {
    LIST(title = R.string.currency_list),
    CONVERTER(title = R.string.app_name),
    INFO(title = R.string.info)
}

@Composable
fun CurrencyConversionApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(backStackEntry?.destination?.route ?: Screen.LIST.name)

    Scaffold(
        topBar = {
            CurrencyConverterTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                openInfo = { navController.navigate(Screen.INFO.name) }
            )
        },
        floatingActionButton = {
            if (currentScreen.name == Screen.LIST.name) {
                ConverterFab(onClick = {
                    navController.navigate(Screen.CONVERTER.name)
                })
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.LIST.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp)
        ) {
            composable(route = Screen.LIST.name) {
                CurrencyListScreenRoot()
            }
            composable(route = Screen.CONVERTER.name) {
                ConverterScreenRoot()
            }
            composable(route = Screen.INFO.name) {
                InfoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterTopBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    openInfo: () -> Unit
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
            if (currentScreen != Screen.INFO) {
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
fun AppPreview() {
    CurrencyConverterTheme {
        CurrencyConversionApp()
    }
}