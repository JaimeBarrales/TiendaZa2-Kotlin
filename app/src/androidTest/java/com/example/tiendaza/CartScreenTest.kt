package com.example.tiendaza.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.ui.viewmodel.CartViewModel
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
        // Given: ViewModel con carrito vacío
        val viewModel = CartViewModel()
        
        // When: Se muestra la pantalla
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: Debería mostrar mensaje de carrito vacío
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertExists()
        composeTestRule.onNodeWithText("¡Agrega productos para comenzar!").assertExists()
    }
    
    @Test
    fun cartScreen_conProductos_muestraLista() {
        // Given: ViewModel con productos
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion2)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: Debería mostrar ambos productos
        composeTestRule.onNodeWithText("iPhone 15").assertExists()
        composeTestRule.onNodeWithText("Samsung Galaxy").assertExists()
    }
    
    @Test
    fun cartScreen_muestraPrecioTotal() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1) // 999
        viewModel.addToCart(testPublicacion2) // 899
        // Total: 1898
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: Debería mostrar el total correcto
        composeTestRule.onNodeWithText("$1898").assertExists()
    }
    
    @Test
    fun cartScreen_muestraCantidadTotal() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion1) // 2 del mismo
        viewModel.addToCart(testPublicacion2) // 1 diferente
        // Total: 3 items
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: Debería mostrar "3 items"
        composeTestRule.onNodeWithText("3 items").assertExists()
    }
    
    @Test
    fun cartScreen_clickEnAumentar_incrementaCantidad() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Verificar cantidad inicial (1)
        composeTestRule.onNodeWithText("1").assertExists()
        
        // Click en botón aumentar (icono Add)
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        
        // Then: La cantidad debería ser 2
        composeTestRule.onNodeWithText("2").assertExists()
    }
    
    @Test
    fun cartScreen_clickEnDisminuir_decrementaCantidad() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion1) // Cantidad inicial: 2
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Verificar cantidad inicial (2)
        composeTestRule.onNodeWithText("2").assertExists()
        
        // Click en botón disminuir (icono Remove)
        composeTestRule.onNodeWithContentDescription("Remove").performClick()
        
        // Then: La cantidad debería ser 1
        composeTestRule.onNodeWithText("1").assertExists()
    }
    
    @Test
    fun cartScreen_clickEnEliminar_quitaProducto() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion2)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Verificar que ambos productos están presentes
        composeTestRule.onNodeWithText("iPhone 15").assertExists()
        composeTestRule.onNodeWithText("Samsung Galaxy").assertExists()
        
        // Click en el botón eliminar (icono Delete) del primer producto
        composeTestRule.onAllNodesWithContentDescription("Delete")[0].performClick()
        
        // Then: El primer producto ya no debería estar
        composeTestRule.onNodeWithText("iPhone 15").assertDoesNotExist()
        composeTestRule.onNodeWithText("Samsung Galaxy").assertExists()
    }
    
    @Test
    fun cartScreen_clickEnVaciarCarrito_eliminaTodo() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        viewModel.addToCart(testPublicacion2)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Verificar que hay productos
        composeTestRule.onNodeWithText("iPhone 15").assertExists()
        
        // Click en "Vaciar Carrito"
        composeTestRule.onNodeWithText("Vaciar Carrito").performClick()
        
        // Then: Debería mostrar mensaje de carrito vacío
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertExists()
    }
    
    @Test
    fun cartScreen_tituloEsCorrecto() {
        // Given
        val viewModel = CartViewModel()
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: El título debe incluir "Mi Carrito"
        composeTestRule.onNodeWithText("Mi Carrito").assertExists()
    }
    
    @Test
    fun cartScreen_botonProcederAlPago_estaPresente() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: El botón debe estar presente
        composeTestRule.onNodeWithText("Proceder al Pago").assertExists()
    }
    
    @Test
    fun cartScreen_precioIndividual_esCorrecto() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: Debería mostrar el precio individual
        composeTestRule.onNodeWithText("$999").assertExists()
    }
    
    @Test
    fun cartScreen_cantidadUno_muestraSingular() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1)
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then: Debería mostrar "1 item" (singular)
        composeTestRule.onNodeWithText("1 item").assertExists()
    }
    
    @Test
    fun cartScreen_actualizacionEnTiempoReal_funcionaCorrectamente() {
        // Given
        val viewModel = CartViewModel()
        
        // When: Se muestra con carrito vacío
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertExists()
        
        // Se agrega un producto fuera del compose
        viewModel.addToCart(testPublicacion1)
        
        composeTestRule.waitForIdle()
        
        // Then: Debería mostrar el producto agregado
        composeTestRule.onNodeWithText("iPhone 15").assertExists()
    }
    
    @Test
    fun cartScreen_multipleProductosMismoCantidad_calculoBienTotal() {
        // Given
        val viewModel = CartViewModel()
        viewModel.addToCart(testPublicacion1) // 999
        viewModel.addToCart(testPublicacion1) // 999
        viewModel.addToCart(testPublicacion1) // 999
        // Total: 2997
        
        // When
        composeTestRule.setContent {
            CartScreen(cartViewModel = viewModel)
        }
        
        // Then
        composeTestRule.onNodeWithText("$2997").assertExists()
        composeTestRule.onNodeWithText("3").assertExists() // Cantidad
    }
}
