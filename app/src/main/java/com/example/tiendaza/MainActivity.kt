package com.example.tiendaza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tiendaza.ui.screens.*
import com.example.tiendaza.ui.theme.TiendazaTheme
import com.example.tiendaza.ui.viewmodel.CartViewModel
import com.example.tiendaza.ui.viewmodel.MainViewModel
import com.example.tiendaza.ui.viewmodel.MainViewModelFactory
import com.example.tiendaza.navigation.BottomBar
import com.example.tiendaza.navigation.BottomNavItem
import com.example.tiendaza.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiendazaTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory())
    val cartViewModel: CartViewModel = viewModel()

    val itemCount by cartViewModel.cartItems.collectAsState()

    val bottomItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Sell,
        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems, itemCount.sumOf { it.cantidad }) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = mainViewModel,
                    onItemClick = { id ->
                        navController.navigate("detail/$id")
                    }
                )
            }

            composable(Routes.SEARCH) {
                SearchScreen(
                    viewModel = mainViewModel,
                    onItemClick = { id ->
                        navController.navigate("detail/$id")
                    }
                )
            }

            composable(
                route = "detail/{publicacionId}",
                arguments = listOf(navArgument("publicacionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("publicacionId") ?: -1L
                DetailScreen(
                    publicacionId = id,
                    mainViewModel = mainViewModel,
                    cartViewModel = cartViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen()
            }

            composable(Routes.SELL) {
                VenderScreen(
                    viewModel = mainViewModel,
                    onCreated = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.CART) {
                CartScreen(cartViewModel)
            }
        }
    }
}