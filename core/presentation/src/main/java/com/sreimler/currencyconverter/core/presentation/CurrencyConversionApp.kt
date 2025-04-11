package com.sreimler.currencyconverter.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sreimler.currencyconverter.core.presentation.theme.CurrencyConverterTheme


@Composable
fun CurrencyConversionApp(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    // TODO: add splash screen
    // TODO: handle loading
    // TODO: display errors, if any

    CurrencyConverterTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CurrencyConversionScaffold(navController, content = content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConversionScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.fromRoute(backStackEntry?.destination?.route)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(currentScreen.titleRes),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            CurrencyConverterBottomBar(
                currentScreen = currentScreen,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                content(paddingValues)
            }
        }
    )
}

@Composable
fun CurrencyConverterBottomBar(
    currentScreen: Screen,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        Screen.bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = stringResource(screen.titleRes)) },
                label = { Text(stringResource(screen.titleRes)) },
                selected = currentScreen.route == screen.route,
                onClick = {
                    if (currentScreen.route != screen.route) {
                        onNavigate(screen.route)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    CurrencyConversionApp(navController = rememberNavController()) { padding ->
        // Fake content for preview
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text("Preview content")
        }
    }
}