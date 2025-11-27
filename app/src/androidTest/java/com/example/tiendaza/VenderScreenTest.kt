package com.example.tiendaza

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tiendaza.data.repository.FakePublicacionRepository
import com.example.tiendaza.ui.screens.VenderScreen
import com.example.tiendaza.ui.viewmodel.MainViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VenderScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun venderScreen_muestraCamposPrincipales() {
        // ViewModel real, pero con repositorio falso (sin llamadas a red)
        val viewModel = MainViewModel(repository = FakePublicacionRepository())

        composeRule.setContent {
            VenderScreen(viewModel = viewModel)
        }

        // Verificamos que los elementos importantes se muestren
        composeRule.onNodeWithText("Publicar un Producto").assertIsDisplayed()
        composeRule.onNodeWithText("Nombre del Producto").assertIsDisplayed()
        composeRule.onNodeWithText("Descripción").assertIsDisplayed()
        composeRule.onNodeWithText("Precio").assertIsDisplayed()
        composeRule.onNodeWithText("📷 Tomar Foto").assertIsDisplayed()
        composeRule.onNodeWithText("🖼️ Subir Imagen").assertIsDisplayed()
        composeRule.onNodeWithText("Vista Previa:").assertIsDisplayed()
        composeRule.onNodeWithText("PUBLICAR").assertIsDisplayed()
    }
}
