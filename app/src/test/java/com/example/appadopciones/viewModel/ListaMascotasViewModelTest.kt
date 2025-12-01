package com.example.appadopciones.viewModel

import com.example.appadopciones.MainDispatcherRule
import com.example.appadopciones.model.Mascota
import com.example.appadopciones.repository.MascotaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaMascotasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `filtro activado muestra solo perros`() = runTest {
        // 1. Given: Preparamos datos en el repositorio
        val repo = MascotaRepository
        val listaPrueba = listOf(
            Mascota(1, "A", "Perro", "2023-01-01", "", true, 1),
            Mascota(2, "B", "Gato", "2023-01-01", "", true, 1),
            Mascota(3, "C", "Perro", "2023-01-01", "", true, 1)
        )
        repo.reemplazarLista(listaPrueba) // Usamos la función auxiliar que creamos

        val viewModel = ListaMascotasViewModel()

        // 2. When: Activamos el filtro
        viewModel.filtrarPorUbicacion(true) // (Recordemos que tu función de ubicación activa el filtro de "Perro")

        // 3. Then: Observamos el flujo
        // .first() toma el valor actual del StateFlow
        val listaFiltrada = viewModel.mascotas.first()

        assertEquals(2, listaFiltrada.size) // Deberían quedar solo 2 perros
        assertTrue(listaFiltrada.all { it.especie == "Perro" })
    }

    @Test
    fun `quitar filtro muestra todo`() = runTest {
        // 1. Given
        val viewModel = ListaMascotasViewModel()
        viewModel.filtrarPorUbicacion(true) // Primero filtramos

        // 2. When
        viewModel.quitarFiltro() // Quitamos filtro

        // 3. Then
        val listaCompleta = viewModel.mascotas.first()
        // Debería haber al menos un Gato si usamos los datos de prueba anteriores o los por defecto
        val hayGatos = listaCompleta.any { it.especie == "Gato" }
        assertTrue(hayGatos)
    }
}