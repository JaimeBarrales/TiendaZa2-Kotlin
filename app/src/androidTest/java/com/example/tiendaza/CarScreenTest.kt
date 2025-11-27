package com.example.tiendaza

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.ui.viewmodel.CartViewModel
import com.example.tiendaza.ui.screens.CartScreen
import org.junit.Rule
import org.junit.Test


class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testPublicacion1 = Publicacion(
        id = 1L,
        titulo = "iPhone 15",
        descripcion = "Smartphone",
        precio = 999,
        urlImg = "img1"
    )

    private val testPublicacion2 = Publicacion(
        id = 2L,
        titulo = "Samsung Galaxy",
        descripcion = "Smartphone",
        precio = 899,
        urlImg = "img2"
    )

    @Test
    fun cartScreen_carritoVacio_muestraMensaje() {
        val viewModel = CartViewModel()

        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Tu carrito está vacío").assertExists()
        composeTestRule.onNodeWithText("¡Agrega productos para comenzar!").assertExists()
    }

    @Test
    fun cartScreen_conProductos_muestraLista() {
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion2)

        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("iPhone 15").assertExists()
        composeTestRule.onNodeWithText("Samsung Galaxy").assertExists()
    }

    @Test
    fun cartScreen_muestraPrecioTotal() {
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1) // 999
        viewModel.addToCart(testPublicacion2) // 899

        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("$1898").assertExists()
    }

    @Test
    fun cartScreen_muestraCantidadTotal() {
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion1) // 2 del mismo
        viewModel.addToCart(testPublicacion2) // 1 diferente
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("3 items").assertExists()
    }

    @Test
    fun cartScreen_clickEnVaciarCarrito_eliminaTodo() {

        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion2)

        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }


        composeTestRule.onNodeWithText("iPhone 15").assertExists()


        composeTestRule.onNodeWithText("Vaciar Carrito").performClick()

        composeTestRule.onNodeWithText("Tu carrito está vacío").assertExists()
    }

    @Test
    fun cartScreen_tituloEsCorrecto() {

        val viewModel = CartViewModel()


        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Mi Carrito").assertExists()
    }

    @Test
    fun cartScreen_botonProcederAlPago_estaPresente() {

        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)


        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }


        composeTestRule.onNodeWithText("Proceder al Pago").assertExists()
    }
}
