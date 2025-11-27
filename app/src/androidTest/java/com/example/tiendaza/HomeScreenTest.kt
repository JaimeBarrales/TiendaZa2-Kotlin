package com.example.tiendaza
import com.example.tiendaza.ui.screens.HomeScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.data.repository.PublicacionRepository
import com.example.tiendaza.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testPublicaciones = listOf(
        Publicacion(
            id = 1L,
            titulo = "iPhone 15 Pro",
            descripcion = "Smartphone Apple",
            precio = 999,
            urlImg = ""
        ),
        Publicacion(
            id = 2L,
            titulo = "Samsung Galaxy S24",
            descripcion = "Smartphone Samsung",
            precio = 899,
            urlImg = ""
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }



    @Test
    fun homeScreen_sinPublicaciones_muestraMensajeVacio() {

        val repository = FakePublicacionRepository(emptyList())
        val viewModel = MainViewModel(repository)


        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }

        composeTestRule.waitForIdle()


        composeTestRule
            .onNodeWithText("No hay productos", substring = true, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun homeScreen_tieneTituloCorrecto() {

        val repository = FakePublicacionRepository(emptyList())
        val viewModel = MainViewModel(repository)


        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }

        // Then
        composeTestRule.onNodeWithText("TiendaZa").assertExists()
    }

    @Test
    fun homeScreen_tieneBotonRefresh() {

        val repository = FakePublicacionRepository(emptyList())
        val viewModel = MainViewModel(repository)


        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onItemClick = {})
        }


        composeTestRule.onNodeWithContentDescription("Recargar").assertExists()
    }


}

private class FakePublicacionRepository(
    private val publicaciones: List<Publicacion> = emptyList()
) : PublicacionRepository {


    override suspend fun getAll(): List<Publicacion> {
        return publicaciones
    }

    override suspend fun getById(id: Long): Publicacion? {
        return publicaciones.find { it.id == id }
    }

    override suspend fun search(query: String): List<Publicacion> {
        return publicaciones.filter {
            it.titulo.contains(query, ignoreCase = true) ||
                    it.descripcion.contains(query, ignoreCase = true)
        }
    }

    override suspend fun create(publicacion: Publicacion): Publicacion {
        return publicacion
    }

    override suspend fun update(id: Long, publicacion: Publicacion): Publicacion {
        return publicacion
    }

    override suspend fun delete(id: Long) {
    }
}
