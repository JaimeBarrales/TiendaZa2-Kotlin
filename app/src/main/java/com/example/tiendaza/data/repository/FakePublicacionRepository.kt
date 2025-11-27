package com.example.tiendaza.data.repository

import com.example.tiendaza.data.model.Publicacion

/**
 * Repositorio Fake para TESTS
 * NO se usa en la app de producción
 */
class FakePublicacionRepository : PublicacionRepository {

    private val publicaciones = mutableListOf(
        Publicacion(1L, "Producto Test 1", "Desc 1", 1000, "img1.jpg"),
        Publicacion(2L, "Producto Test 2", "Desc 2", 2000, "img2.jpg")
    )

    override suspend fun getAll(): List<Publicacion> = publicaciones

    override suspend fun getById(id: Long): Publicacion? =
        publicaciones.find { it.id == id }

    override suspend fun search(query: String): List<Publicacion> =
        publicaciones.filter { it.titulo.contains(query, ignoreCase = true) }

    override suspend fun create(publicacion: Publicacion): Publicacion {
        val nueva = publicacion.copy(id = (publicaciones.size + 1).toLong())
        publicaciones.add(nueva)
        return nueva
    }

    override suspend fun update(id: Long, publicacion: Publicacion): Publicacion {
        val index = publicaciones.indexOfFirst { it.id == id }
        if (index != -1) {
            publicaciones[index] = publicacion
        }
        return publicacion
    }

    override suspend fun delete(id: Long) {
        publicaciones.removeIf { it.id == id }
    }
}