package com.example.tiendaza.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.data.repository.PublicacionRepository
import com.example.tiendaza.ui.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: PublicacionRepository
    private lateinit var viewModel: MainViewModel

    private val testPublicaciones = listOf(
        Publicacion(1L, "iPhone 15", "Apple smartphone", 999, "img1"),
        Publicacion(2L, "iPhone 14", "Older Apple phone", 799, "img2"),
        Publicacion(3L, "Samsung Galaxy", "Samsung phone", 899, "img3")
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
    fun searchScreen_inicialmenteMuestraTodasLasPublicaciones() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search(any()) } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then: Deberían aparecer todos los productos
        composeTestRule.onNodeWithText("iPhone 15").assertExists()
        composeTestRule.onNodeWithText("Samsung Galaxy").assertExists()
    }

    @Test
    fun searchScreen_tieneCampoDeBusqueda() {
        // Given
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        // Then: El campo de búsqueda debe estar presente
        composeTestRule.onNodeWithText("Buscar productos...").assertExists()
    }

    @Test
    fun searchScreen_tieneBotonBuscar() {
        // Given
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        // Then: El botón de búsqueda debe estar presente
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test
    fun searchScreen_escribirEnBusqueda_actualizaTexto() {
        // Given
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        // Escribir en el campo de búsqueda
        composeTestRule
            .onNodeWithText("Buscar productos...")
            .performTextInput("iPhone")

        // Then: El texto debe aparecer en el campo
        composeTestRule.onNodeWithText("iPhone").assertExists()
    }

    @Test
    fun searchScreen_clickEnBuscar_realizaBusqueda() {
        // Given
        val resultadosBusqueda = listOf(
            testPublicaciones[0], // iPhone 15
            testPublicaciones[1]  // iPhone 14
        )

        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search("iPhone") } returns resultadosBusqueda
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Escribir búsqueda
        composeTestRule
            .onNodeWithText("Buscar productos...")
            .performTextInput("iPhone")

        // Click en buscar
        composeTestRule.onNodeWithContentDescription("Search").performClick()

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then: Debería haber llamado a search con el término correcto
        coVerify { mockRepository.search("iPhone") }
    }

    @Test
    fun searchScreen_sinResultados_muestraMensaje() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search("Nintendo") } returns emptyList()
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Realizar búsqueda sin resultados
        composeTestRule
            .onNodeWithText("Buscar productos...")
            .performTextInput("Nintendo")
        composeTestRule.onNodeWithContentDescription("Search").performClick()

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then: Debería mostrar mensaje de no resultados
        composeTestRule.onNodeWithText("No se encontraron resultados").assertExists()
        composeTestRule.onNodeWithText("Intenta con otras palabras").assertExists()
    }

    @Test
    fun searchScreen_tituloEsCorrecto() {
        // Given
        coEvery { mockRepository.getAll() } returns emptyList()
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        // Then
        composeTestRule.onNodeWithText("Buscar Productos").assertExists()
    }

    @Test
    fun searchScreen_clickEnProducto_llamaCallback() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search(any()) } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)

        var clickedId: Long? = null

        // When
        composeTestRule.setContent {
            SearchScreen(
                viewModel = viewModel,
                onItemClick = { id -> clickedId = id }
            )
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Click en el primer resultado
        composeTestRule.onAllNodesWithText("Ver")[0].performClick()

        // Then
        assert(clickedId != null)
        assert(clickedId == 1L)
    }

    @Test
    fun searchScreen_duranteBusqueda_muestraIndicadorCarga() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search(any()) } coAnswers {
            kotlinx.coroutines.delay(1000)
            emptyList()
        }
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Iniciar búsqueda
        composeTestRule
            .onNodeWithText("Buscar productos...")
            .performTextInput("test")
        composeTestRule.onNodeWithContentDescription("Search").performClick()

        // Then: Debería mostrar indicador de carga
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun searchScreen_resultados_muestranPrecios() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search(any()) } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then: Los precios deberían estar visibles
        composeTestRule.onNodeWithText("$999").assertExists()
        composeTestRule.onNodeWithText("$799").assertExists()
        composeTestRule.onNodeWithText("$899").assertExists()
    }

    @Test
    fun searchScreen_busquedaVacia_devuelveTodos() {
        // Given
        coEvery { mockRepository.getAll() } returns testPublicaciones
        coEvery { mockRepository.search("") } returns testPublicaciones
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Buscar con campo vacío
        composeTestRule.onNodeWithContentDescription("Search").performClick()

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then: Deberían aparecer todos los productos
        composeTestRule.onNodeWithText("iPhone 15").assertExists()
        composeTestRule.onNodeWithText("Samsung Galaxy").assertExists()
    }

    @Test
    fun searchScreen_gridDosColumnas_funcionaCorrectamente() {
        // Given
        val muchasPublicaciones = (1..6).map {
            Publicacion(it.toLong(), "Producto $it", "Descripción", 100 * it, "img$it")
        }

        coEvery { mockRepository.getAll() } returns muchasPublicaciones
        coEvery { mockRepository.search(any()) } returns muchasPublicaciones
        viewModel = MainViewModel(mockRepository)

        // When
        composeTestRule.setContent {
            SearchScreen(viewModel = viewModel, onItemClick = {})
        }

        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then: Todos deberían estar visibles
        composeTestRule.onNodeWithText("Producto 1").assertExists()
        composeTestRule.onNodeWithText("Producto 6").assertExists()
    }
}
