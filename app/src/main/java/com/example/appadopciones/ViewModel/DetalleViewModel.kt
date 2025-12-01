package com.example.appadopciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appadopciones.model.Mascota
import com.example.appadopciones.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DetalleMascotaViewModel : ViewModel() {
    private val repositorio = MascotaRepository

    private val _mascotaDetalle = MutableStateFlow<Mascota?>(null)
    val mascotaDetalle: StateFlow<Mascota?> = _mascotaDetalle.asStateFlow()

    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    fun cargarMascota(mascotaId: Int) {
        viewModelScope.launch {
            _estaCargando.value = true
            val mascotaEncontrada = repositorio.obtenerMascotas().find { it.id == mascotaId }
            _mascotaDetalle.value = mascotaEncontrada
            _estaCargando.value = false
        }
    }

    fun calcularEdad(fechaNacimiento: String): String {
        return try {
            val fechaNac = LocalDate.parse(fechaNacimiento, DateTimeFormatter.ISO_DATE)
            val hoy = LocalDate.now()

            val anos = ChronoUnit.YEARS.between(fechaNac, hoy)
            if (anos >= 1) {
                "$anos años"
            } else {
                val meses = ChronoUnit.MONTHS.between(fechaNac, hoy)
                "$meses meses"
            }
        } catch (e: Exception) {
            "Fecha no válida"
        }
    }
}