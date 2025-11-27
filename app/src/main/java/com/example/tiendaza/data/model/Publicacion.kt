package com.example.tiendaza.data.model

import com.google.gson.annotations.SerializedName

data class Publicacion(
    @SerializedName("id")
    val id: Long,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("precio")
    val precio: Int,

    @SerializedName("urlImg")
    val urlImg: String
)