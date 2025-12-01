package com.example.appadopciones.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appadopciones.repository.MascotaRepository
import com.example.appadopciones.model.PublicacionModel
import com.example.appadopciones.model.MensajeError
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.appadopciones.repository.RemoteMascotaRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PublicarViewModel : ViewModel() {
    private val repositorio = MascotaRepository

    var idMascotaAEditar: Int? by mutableStateOf(null)

    var publicacion: PublicacionModel by mutableStateOf( PublicacionModel() )
        private set

    var mensajesError: MensajeError by mutableStateOf( repositorio.getMensajesErrorInicial() )
        private set

    var publicacionExitosa by mutableStateOf(false)

    var uriFotoSeleccionada by mutableStateOf<Uri?>(null)
        private set

    // --- Carga y Limpieza ---

    fun cargarDatosParaEdicion(id: Int) {
        val mascota = repositorio.obtenerMascotas().find { it.id == id }
        if (mascota != null) {
            idMascotaAEditar = id
            publicacion = PublicacionModel(
                nombreMascota = mascota.nombre,
                especie = mascota.especie,
                fechaNacimiento = mascota.fechaNacimiento,
                descripcion = mascota.descripcion,
                telefonoContacto = mascota.telefonoContacto,
                nombreContacto = ""
            )
        }
    }

    fun limpiarFormulario() {
        publicacion = PublicacionModel()
        mensajesError = MensajeError()
        uriFotoSeleccionada = null
        idMascotaAEditar = null
        publicacionExitosa = false
    }

    fun resetearEstado() {
        limpiarFormulario()
    }

    // --- Handlers de Formulario ---

    fun onUriFotoSeleccionada(uri: Uri?) { uriFotoSeleccionada = uri }
    fun onNombreMascotaChange(nombre: String) { publicacion = publicacion.copy(nombreMascota = nombre) }
    fun onEspecieChange(especie: String) { publicacion = publicacion.copy(especie = especie) }
    fun onFechaNacimientoChange(fecha: String) { publicacion = publicacion.copy(fechaNacimiento = fecha) }
    fun onDescripcionChange(descripcion: String) { publicacion = publicacion.copy(descripcion = descripcion) }
    fun onTelefonoContactoChange(telefono: String) { publicacion = publicacion.copy(telefonoContacto = telefono) }
    fun onNombreContactoChange(nombre: String) { publicacion = publicacion.copy(nombreContacto = nombre) }

    // --- Lógica y Validación ---

    fun calcularEdad(): String {
        return try {
            val fechaNac = LocalDate.parse(publicacion.fechaNacimiento, DateTimeFormatter.ISO_DATE)
            val hoy = LocalDate.now()

            val anos = ChronoUnit.YEARS.between(fechaNac, hoy)
            if (anos >= 1) {
                "$anos años"
            } else {
                val meses = ChronoUnit.MONTHS.between(fechaNac, hoy)
                "$meses meses"
            }
        } catch (e: Exception) {
            "Fecha no seleccionada"
        }
    }

    fun verificarNombreMascota(): Boolean = repositorio.validacionNoVacio(publicacion.nombreMascota).also { if(!it) mensajesError = mensajesError.copy(errorNombreMascota = "Requerido") else mensajesError = mensajesError.copy(errorNombreMascota = "") }

    fun verificarEspecie(): Boolean = repositorio.validacionNoVacio(publicacion.especie).also { if(!it) mensajesError = mensajesError.copy(errorEspecie = "Requerido") else mensajesError = mensajesError.copy(errorEspecie = "") }

    fun verificarFechaNacimiento(): Boolean {
        return try {
            val fecha = LocalDate.parse(publicacion.fechaNacimiento, DateTimeFormatter.ISO_DATE)
            val esValido = fecha.isBefore(LocalDate.now())
            if (!esValido) mensajesError = mensajesError.copy(errorFechaNacimiento = "Fecha inválida")
            else mensajesError = mensajesError.copy(errorFechaNacimiento = "")
            esValido
        } catch (e: Exception) {
            mensajesError = mensajesError.copy(errorFechaNacimiento = "Seleccione una fecha")
            false
        }
    }

    fun verificarDescripcion(): Boolean {
        val esValido = publicacion.descripcion.length >= 10
        if (!esValido) mensajesError = mensajesError.copy(errorDescripcion = "Mínimo 10 caracteres")
        else mensajesError = mensajesError.copy(errorDescripcion = "")
        return esValido
    }

    fun verificarTelefonoContacto(): Boolean = repositorio.validacionTelefono(publicacion.telefonoContacto).also { if(!it) mensajesError = mensajesError.copy(errorTelefonoContacto = "Inválido") else mensajesError = mensajesError.copy(errorTelefonoContacto = "") }

    fun verificarFoto(): Boolean {
        return uriFotoSeleccionada != null || idMascotaAEditar != null
    }

    fun publicarMascota() {
        val nombreValido = verificarNombreMascota()
        val especieValida = verificarEspecie()
        val fechaValida = verificarFechaNacimiento()
        val descripcionValida = verificarDescripcion()
        val telefonoValido = verificarTelefonoContacto()
        val fotoValida = verificarFoto()

        // Resetear error general antes de la validación
        mensajesError = mensajesError.copy(errorGeneral = "")

        if (nombreValido && especieValida && fechaValida && descripcionValida && telefonoValido && fotoValida) {
            viewModelScope.launch {
                try {
                    val urlImagenString = uriFotoSeleccionada?.toString()

                    if (idMascotaAEditar != null) {
                        withContext(Dispatchers.IO) {
                            RemoteMascotaRepository.actualizarMascota(
                                idMascotaAEditar!!, publicacion, urlImagenString
                            )
                        }
                    } else {
                        withContext(Dispatchers.IO) {
                            RemoteMascotaRepository.crearMascota(publicacion, urlImagenString)
                        }
                    }

                    val listaRemota = withContext(Dispatchers.IO) {
                        RemoteMascotaRepository.fetchMascotas()
                    }
                    repositorio.reemplazarLista(listaRemota)

                    publicacionExitosa = true

                } catch (e: Exception) {
                    e.printStackTrace()
                    mensajesError = mensajesError.copy(errorGeneral = "Error de red al publicar. Intente más tarde.")
                }
            }
        } else {
            // Si falla, mostramos el error general
            mensajesError = mensajesError.copy(errorGeneral = "Revisa los campos obligatorios y asegúrate de tomar una foto.")
        }
    }
}