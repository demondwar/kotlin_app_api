package com.example.appadopciones.model

data class Mascota(
    val id: Int,
    val nombre: String,
    val especie: String,
    val fechaNacimiento: String,
    val urlImagen: String,
    val estaEnAdopcion: Boolean = true,
    val idUsuarioPublicador: Int,
    val telefonoContacto: String = "",
    val descripcion: String = ""
)