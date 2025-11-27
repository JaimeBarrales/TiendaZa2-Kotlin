package com.example.tiendaza.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("Home", Routes.HOME, Icons.Filled.Home)
    object Search : BottomNavItem("Buscar", Routes.SEARCH, Icons.Filled.Search)
    object Sell : BottomNavItem("Vender", Routes.SELL, Icons.Filled.Add)
    object Cart : BottomNavItem("Carrito", Routes.CART, Icons.Filled.ShoppingCart)
    object Profile : BottomNavItem("Perfil", Routes.PROFILE, Icons.Filled.Person)
}