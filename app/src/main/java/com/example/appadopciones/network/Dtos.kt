package com.example.appadopciones.network

data class MascotaDto(
    val id: Int,
    val nombre: String,
    val especie: String,
    val fechaNacimiento: String? = null,
    val urlImagen: String,
    val estaEnAdopcion: Boolean = true,
    val idUsuarioPublicador: Int,
    val telefonoContacto: String? = null,
    val descripcion: String? = null
)

data class PublicacionDto(
    val nombre: String,
    val especie: String,
    val fechaNacimiento: String? = null,
    val descripcion: String? = null,
    val nombreContacto: String? = null,
    val telefonoContacto: String? = null,
    val urlImagen: String? = null,
    val idUsuarioPublicador: Int
)