package com.example.tiendaza.data.repository

import com.example.tiendaza.data.model.Publicacion

interface PublicacionRepository {
    suspend fun getAll(): List<Publicacion>
    suspend fun getById(id: Long): Publicacion?
    suspend fun search(query: String): List<Publicacion>
    suspend fun create(publicacion: Publicacion): Publicacion
    suspend fun update(id: Long, publicacion: Publicacion): Publicacion
    suspend fun delete(id: Long)
}