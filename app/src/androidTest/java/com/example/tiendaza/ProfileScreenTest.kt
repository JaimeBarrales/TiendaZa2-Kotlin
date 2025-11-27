package com.example.tiendaza

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tiendaza.ui.screens.ProfileScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun profileScreen_muestraCamposYBoton() {
        // When
        composeRule.setContent {
            ProfileScreen()
        }

        composeRule.waitForIdle()

        // Then
        composeRule.onNodeWithText("Bienvenido").assertIsDisplayed()
        composeRule.onNodeWithText("Inicia sesión en tu cuenta").assertIsDisplayed()
        composeRule.onNodeWithText("Correo").assertIsDisplayed()
        composeRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeRule.onNodeWithText("inicio sesion").assertIsDisplayed()
    }

    @Test
    fun profileScreen_permiteIngresarCorreoYContrasena() {
        // When
        composeRule.setContent {
            ProfileScreen()
        }

        composeRule.waitForIdle()

        // Ingresar datos
        composeRule.onNodeWithText("Correo")
            .performTextInput("test@correo.cl")

        composeRule.onNodeWithText("Contraseña")
            .performTextInput("123456")

        // Then - Los textos deben estar visibles
        composeRule.onNodeWithText("test@correo.cl").assertExists()
        // La contraseña estará oculta, pero el campo existe
        composeRule.onNode(hasText("Contraseña")).assertExists()
    }

    @Test
    fun profileScreen_muestraOpcionOlvidoContrasena() {
        // When
        composeRule.setContent {
            ProfileScreen()
        }

        composeRule.waitForIdle()

        // Then
        composeRule.onNodeWithText("Olvido su contraseña?").assertIsDisplayed()
    }

    @Test
    fun profileScreen_muestraOpcionIniciarConOtrosMetodos() {
        // When
        composeRule.setContent {
            ProfileScreen()
        }

        composeRule.waitForIdle()

        // Then
        composeRule.onNodeWithText("Iniciar sesion con").assertIsDisplayed()
    }

    @Test
    fun profileScreen_botonInicioSesionEsClickable() {
        // When
        composeRule.setContent {
            ProfileScreen()
        }

        composeRule.waitForIdle()

        // Then - El botón debe existir y ser clickeable
        composeRule.onNodeWithText("inicio sesion")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun profileScreen_enlaceOlvidoContrasenaEsClickable() {
        // When
        composeRule.setContent {
            ProfileScreen()
        }

        composeRule.waitForIdle()

        // Then
        composeRule.onNodeWithText("Olvido su contraseña?")
            .assertExists()
            .assertHasClickAction()
    }
}