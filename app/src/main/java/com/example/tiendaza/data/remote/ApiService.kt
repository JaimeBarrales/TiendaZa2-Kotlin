package com.example.tiendaza.data.remote

import com.example.tiendaza.data.model.Publicacion
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @GET("publicaciones")
    suspend fun getPublicaciones(): List<Publicacion>

    @GET("publicaciones/{id}")
    suspend fun getPublicacionById(@Path("id") id: Long): Publicacion

    @GET("publicaciones/search")
    suspend fun searchPublicaciones(@Query("query") query: String?): List<Publicacion>

    @POST("publicaciones")
    suspend fun createPublicacion(@Body publicacion: Publicacion): Publicacion

    @Multipart
    @POST("publicaciones/con-imagen")
    suspend fun createPublicacionWithImage(
        @Part image: MultipartBody.Part,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("precio") precio: RequestBody
    ): Publicacion

    @PUT("publicaciones/{id}")
    suspend fun updatePublicacion(
        @Path("id") id: Long,
        @Body publicacion: Publicacion
    ): Publicacion

    @DELETE("publicaciones/{id}")
    suspend fun deletePublicacion(@Path("id") id: Long)
}