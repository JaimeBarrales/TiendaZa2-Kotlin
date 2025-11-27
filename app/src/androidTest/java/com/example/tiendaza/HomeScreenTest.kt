package com.example.tiendaza

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.data.repository.PublicacionRepository
import com.example.tiendaza.ui.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: PublicacionRepository
    private lateinit var viewModel: MainViewModel
    
    private val testPublicaciones = listOf(
        Publicacion(
            id = 1L,
            titulo = "iPhone 15 Pro",
            descripcion = "Smartphone Apple",
            precio = 999,
            urlImg = "test_img_1"
        ),
        Publicacion(
            id = 2L,
            titulo = "Samsung Galaxy S24",
            descripcion = "Smartphone Samsung",
            precio = 899,
            urlImg = "test_img_2"
        )
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun homeScreen_cuandoCarga_muestraIndicadorDeCarga() {
        // Given: Repository que tarda en responder
        coEvery { mockRepository.getAll() } coAnswers {
            kotlinx.coroutines.delay(1000)
            testPublicaciones
        }
        viewModel = MainViewModel(mockRepository)
        
        // When: Se muestra la pantalla
        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel,
                onItemClick = {}
            )
        }
        
        // Then: Debería mostrar el indicador de carga
        composeTestRule.onNodeWithText("Cargando productos...").assertExists()
    }
    
    @Test
    fun homeScreen_conPublicaciones_muestraLista() {
        // Given: Repository con publicaciones
        coEvery { mockRepository.getAll() } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)
        
        // When: Se muestra la pantalla y se espera a que cargue
        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel,
                onItemClick = {}
            )
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Then: Deberían aparecer los títulos de las publicaciones
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertExists()
        composeTestRule.onNodeWithText("Samsung Galaxy S24").assertExists()
    }
    
    @Test
    fun homeScreen_conPublicaciones_muestraPrecios() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Then: Los precios deberían estar visibles
        composeTestRule.onNodeWithText("$999").assertExists()
        composeTestRule.onNodeWithText("$899").assertExists()
    }
    
    @Test
    fun homeScreen_sinPublicaciones_muestraMensajeVacio() {
        // Given: Repository vacío
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Then: Debería mostrar mensaje de lista vacía
        composeTestRule.onNodeWithText("No hay productos aún").assertExists()
        composeTestRule.onNodeWithText("¡Sé el primero en publicar!").assertExists()
    }
    
    @Test
    fun homeScreen_conError_muestraMensajeDeError() {
        // Given: Repository que lanza error
        coEvery { mockRepository.getAll() } throws Exception("Network error")
        viewModel = MainViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Then: Debería mostrar mensaje de error
        composeTestRule.onNodeWithText("Ups! Algo salió mal").assertExists()
        composeTestRule.onNodeWithText("Network error").assertExists()
    }
    
    @Test
    fun homeScreen_clickEnRecargar_recargaPublicaciones() {
        // Given: Repository con error inicial
        var callCount = 0
        coEvery { mockRepository.getAll() } answers {
            callCount++
            if (callCount == 1) throw Exception("Error")
            else testPublicaciones
        }
        viewModel = MainViewModel(mockRepository)
        
        // When: Se hace click en reintentar
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Click en el botón reintentar
        composeTestRule.onNodeWithText("Reintentar").performClick()
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Then: Debería mostrar las publicaciones
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertExists()
    }
    
    @Test
    fun homeScreen_clickEnProducto_llamaCallback() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)
        var clickedId: Long? = null
        
        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel,
                onItemClick = { id -> clickedId = id }
            )
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Click en el botón "Ver" del primer producto
        composeTestRule.onAllNodesWithText("Ver")[0].performClick()
        
        // Then: El callback debería haber sido llamado con el ID correcto
        assert(clickedId != null)
        assert(clickedId == 1L)
    }
    
    @Test
    fun homeScreen_tieneTituloCorrecto() {
        // Given
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        // Then: El título debe estar presente
        composeTestRule.onNodeWithText("TiendaZa").assertExists()
    }
    
    @Test
    fun homeScreen_tieneBotonRefresh() {
        // Given
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        // Then: El botón de refresh debe estar presente
        composeTestRule.onNodeWithContentDescription("Recargar").assertExists()
    }
    
    @Test
    fun homeScreen_gridDosColumnas_muestraProductosCorrectamente() {
        // Given: 4 productos para ver el grid completo
        val publicaciones = listOf(
            Publicacion(1L, "Producto 1", "Desc 1", 100, "img1"),
            Publicacion(2L, "Producto 2", "Desc 2", 200, "img2"),
            Publicacion(3L, "Producto 3", "Desc 3", 300, "img3"),
            Publicacion(4L, "Producto 4", "Desc 4", 400, "img4")
        )
        
        coEvery { mockRepository.getAll() } returns publicaciones
        viewModel = MainViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        
        // Then: Todos los productos deberían estar visibles
        composeTestRule.onNodeWithText("Producto 1").assertExists()
        composeTestRule.onNodeWithText("Producto 2").assertExists()
        composeTestRule.onNodeWithText("Producto 3").assertExists()
        composeTestRule.onNodeWithText("Producto 4").assertExists()
    }
}
