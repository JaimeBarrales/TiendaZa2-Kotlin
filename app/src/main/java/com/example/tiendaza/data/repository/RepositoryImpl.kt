package com.example.tiendaza.data.repository

import com.example.tiendaza.data.model.Publicacion
import com.example.tiendaza.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PublicacionRepositoryImpl : PublicacionRepository {

    private val apiService = ApiClient.apiService

    override suspend fun getAll(): List<Publicacion> = withContext(Dispatchers.IO) {
        try {
            apiService.getPublicaciones()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getById(id: Long): Publicacion? = withContext(Dispatchers.IO) {
        try {
            apiService.getPublicacionById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun search(query: String): List<Publicacion> = withContext(Dispatchers.IO) {
        try {
            apiService.searchPublicaciones(query)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun create(publicacion: Publicacion): Publicacion = withContext(Dispatchers.IO) {
        apiService.createPublicacion(publicacion)
    }

    override suspend fun update(id: Long, publicacion: Publicacion): Publicacion = withContext(Dispatchers.IO) {
        apiService.updatePublicacion(id, publicacion)
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        apiService.deletePublicacion(id)
    }
}