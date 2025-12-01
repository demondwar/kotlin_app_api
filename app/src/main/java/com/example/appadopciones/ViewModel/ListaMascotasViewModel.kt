package com.example.appadopciones.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appadopciones.model.Mascota
import com.example.appadopciones.repository.MascotaRepository
import com.example.appadopciones.repository.RemoteMascotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ListaMascotasViewModel : ViewModel() {
    private val repositorio = MascotaRepository

    private val _estaFiltradoPorUbicacion = MutableStateFlow(false)
    val estaFiltradoPorUbicacion: StateFlow<Boolean> = _estaFiltradoPorUbicacion

    val mascotas: StateFlow<List<Mascota>> = repositorio.listaMascotas
        .combine(_estaFiltradoPorUbicacion) { listaMascotas, estaFiltrado ->
            if (estaFiltrado) {
                listaMascotas.filter { it.especie.contains("Perro", ignoreCase = true) }
            } else {
                listaMascotas
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repositorio.listaMascotas.value
        )
    init {
        viewModelScope.launch {
            sincronizarDesdeApi()
        }
    }

    fun sincronizarDesdeApi() {
        viewModelScope.launch {
            try {
                val remotas = withContext(Dispatchers.IO) {
                    RemoteMascotaRepository.fetchMascotas()
                }
                repositorio.reemplazarLista(remotas)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun filtrarPorUbicacion(ubicacionValida: Boolean) {
        _estaFiltradoPorUbicacion.value = ubicacionValida
    }

    fun quitarFiltro() {
        _estaFiltradoPorUbicacion.value = false
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

    fun eliminarMascota(mascotaId: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RemoteMascotaRepository.eliminarMascota(mascotaId)
                }
                repositorio.eliminarMascota(mascotaId)
                sincronizarDesdeApi()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}