package com.example.appadopciones.repository

import com.example.appadopciones.model.Mascota
import com.example.appadopciones.model.PublicacionModel
import com.example.appadopciones.model.MensajeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri

// 👇 TU ID DE USUARIO SIMULADO
const val ID_USUARIO_ACTUAL = 100

object MascotaRepository {
    private val _listaMascotas = MutableStateFlow(
        listOf(
            // Mascotas de prueba con otro ID (200) para que no puedas borrarlas
            Mascota(1, "Bimbo", "Perro", fechaNacimiento = "2023-01-01", urlImagen = "drawable://perro", idUsuarioPublicador = 200, descripcion = "Es un perro muy juguetón."),
            Mascota(2, "Luna", "Gato", fechaNacimiento = "2024-05-15", urlImagen = "drawable://gato", idUsuarioPublicador = 200, descripcion = "Una gata tranquila y hogareña.")
        )
    )
    val listaMascotas: StateFlow<List<Mascota>> = _listaMascotas.asStateFlow()

    private var proximoId = 3

    fun obtenerMascotas(): List<Mascota> = listaMascotas.value

    fun agregarMascota(publicacion: PublicacionModel, uriFoto: Uri?) {

        val imagenUri = uriFoto?.toString() ?: "error_uri_nula"

        val nuevaMascota = Mascota(
            id = proximoId++,
            nombre = publicacion.nombreMascota,
            especie = publicacion.especie,
            fechaNacimiento = publicacion.fechaNacimiento,
            urlImagen = imagenUri,
            idUsuarioPublicador = ID_USUARIO_ACTUAL, // Asignar tu ID localmente también
            descripcion = publicacion.descripcion
        )
        _listaMascotas.value = _listaMascotas.value + nuevaMascota
    }

    fun actualizarMascota(id: Int, publicacion: PublicacionModel, uriFoto: Uri?) {
        val listaActual = _listaMascotas.value.toMutableList()
        val index = listaActual.indexOfFirst { it.id == id }

        if (index != -1) {
            val mascotaAntigua = listaActual[index]
            val imagenFinal = uriFoto?.toString() ?: mascotaAntigua.urlImagen

            val mascotaActualizada = mascotaAntigua.copy(
                nombre = publicacion.nombreMascota,
                especie = publicacion.especie,
                fechaNacimiento = publicacion.fechaNacimiento,
                urlImagen = imagenFinal,
                descripcion = publicacion.descripcion
            )

            listaActual[index] = mascotaActualizada
            _listaMascotas.value = listaActual
        }
    }

    fun eliminarMascota(mascotaId: Int) {
        _listaMascotas.value = _listaMascotas.value.filter { it.id != mascotaId }
    }

    fun getMensajesErrorInicial(): MensajeError = MensajeError()

    fun validacionNoVacio(campo: String): Boolean {
        return campo.isNotBlank()
    }

    fun validacionTelefono(telefono: String): Boolean {
        return telefono.matches(Regex("^[0-9]{8,12}\$"))
    }

    fun reemplazarLista(nuevaLista: List<Mascota>) {
        _listaMascotas.value = nuevaLista
        val maxId = nuevaLista.maxOfOrNull { it.id } ?: 0
        if (maxId >= proximoId) proximoId = maxId + 1
    }
}