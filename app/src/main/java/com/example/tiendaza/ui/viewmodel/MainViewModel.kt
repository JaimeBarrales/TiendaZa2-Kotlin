package com.example.tiendaza.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.data.remote.ApiClient
import com.example.tiendaza.data.repository.PublicacionRepository
import com.example.tiendaza.data.repository.PublicacionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(
    private val repository: PublicacionRepository = PublicacionRepositoryImpl()
) : ViewModel() {

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadPublicaciones()
    }

    fun loadPublicaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = repository.getAll()
                _publicaciones.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cargar publicaciones"
                _publicaciones.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPublicacion(id: Long): Publicacion? {
        return _publicaciones.value.find { it.id == id }
    }

    fun searchPublicaciones(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = repository.search(query)
                _publicaciones.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearPublicacionConImagen(
        image: MultipartBody.Part,
        titulo: RequestBody,
        descripcion: RequestBody,
        precio: RequestBody,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                android.util.Log.d("Upload", "Iniciando upload...")

                val nuevaPublicacion = ApiClient.apiService.createPublicacionWithImage(
                    image = image,
                    titulo = titulo,
                    descripcion = descripcion,
                    precio = precio
                )

                android.util.Log.d("Upload", "✅ Publicación creada: ${nuevaPublicacion.id}")
                loadPublicaciones()
                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("Upload", "❌ Error: ${e.message}", e)
                onError(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }
}