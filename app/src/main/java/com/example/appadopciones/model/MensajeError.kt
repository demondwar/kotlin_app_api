package com.example.appadopciones.model

data class MensajeError(
    var errorNombreMascota: String = "",
    var errorEspecie: String = "",
    var errorFechaNacimiento: String = "",
    var errorDescripcion: String = "",
    var errorNombreContacto: String = "",
    var errorTelefonoContacto: String = "",
    var errorGeneral: String = ""
)