package com.example.tiendaza.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.tiendaza.ui.viewmodel.MainViewModel
import com.example.tiendaza.utils.bitmapToMultipart
import com.example.tiendaza.utils.uriToMultipart
import com.example.tiendaza.utils.toPlainTextRequestBody
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenderScreen(
    viewModel: MainViewModel,
    onCreated: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var nombreProducto by remember { mutableStateOf("") }
    var descripcionProducto by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            bitmap = bmp
            imageUri = null
        }
    }

    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            bitmap = null
        }
    }

    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado.") }
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectImageLauncher.launch("image/*")
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de galería denegado.") }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Publicar un Producto") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nombreProducto,
                onValueChange = { nombreProducto = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcionProducto,
                onValueChange = { descripcionProducto = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            OutlinedTextField(
                value = precioTexto,
                onValueChange = { precioTexto = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) takePictureLauncher.launch(null)
                        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) { Text("📷 Tomar Foto") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            galleryPermission
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) selectImageLauncher.launch("image/*")
                        else galleryPermissionLauncher.launch(galleryPermission)
                    }
                ) { Text("🖼️ Subir Imagen") }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Vista Previa:",
                style = MaterialTheme.typography.titleMedium
            )

            when {
                bitmap != null -> {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Vista previa de la foto tomada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                imageUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Vista previa de la imagen seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ninguna imagen seleccionada")
                    }
                }
            }

            Button(
                onClick = {
                    if (nombreProducto.isBlank() || descripcionProducto.isBlank() || precioTexto.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos")
                        }
                        return@Button
                    }
                    val precioInt = precioTexto.toIntOrNull()
                    if (precioInt == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Precio inválido")
                        }
                        return@Button
                    }
                    if (imageUri == null && bitmap == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Selecciona o toma una imagen")
                        }
                        return@Button
                    }

                    val imagePart = if (bitmap != null) {
                        bitmapToMultipart(context, bitmap!!)
                    } else {
                        uriToMultipart(context, imageUri!!)
                    }

                    val tituloPart = nombreProducto.toPlainTextRequestBody()
                    val descripcionPart = descripcionProducto.toPlainTextRequestBody()
                    val precioPart = precioInt.toString().toPlainTextRequestBody()

                    viewModel.crearPublicacionConImagen(
                        image = imagePart,
                        titulo = tituloPart,
                        descripcion = descripcionPart,
                        precio = precioPart,
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ Publicación creada exitosamente")
                            }
                            nombreProducto = ""
                            descripcionProducto = ""
                            precioTexto = ""
                            imageUri = null
                            bitmap = null

                            onCreated()
                        },
                        onError = { msg ->
                            scope.launch {
                                snackbarHostState.showSnackbar("❌ Error: $msg")
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("PUBLICAR")
            }
        }
    }
}