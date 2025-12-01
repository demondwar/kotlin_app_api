package com.example.appadopciones.viewModel

import com.example.appadopciones.MainDispatcherRule
import com.example.appadopciones.model.Mascota
import com.example.appadopciones.model.PublicacionModel
import com.example.appadopciones.repository.MascotaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleMascotaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `cargarMascota encuentra la mascota correcta por ID`() = runTest {
        // 1. Given: Preparamos el repositorio con una mascota conocida
        val repo = MascotaRepository
        val mascotaPrueba = PublicacionModel(
            nombreMascota = "MascotaTest",
            especie = "Gato",
            fechaNacimiento = "2020-01-01",
            descripcion = "Descripcion de prueba",
            telefonoContacto = "12345678"
        )
        repo.agregarMascota(mascotaPrueba, null)

        // Obtenemos el ID real que se generó
        val idGenerado = repo.obtenerMascotas().last().id

        val viewModel = DetalleMascotaViewModel()

        // 2. When: Cargamos esa mascota
        viewModel.cargarMascota(idGenerado)
        advanceUntilIdle() // Esperamos a que la corrutina termine

        // 3. Then: Verificamos que el estado tenga la mascota correcta
        val mascotaCargada = viewModel.mascotaDetalle.first()
        assertNotNull(mascotaCargada)
        assertEquals("MascotaTest", mascotaCargada?.nombre)
        assertEquals(false, viewModel.estaCargando.value)
    }

    @Test
    fun `calcularEdad en detalle retorna formato correcto`() {
        val viewModel = DetalleMascotaViewModel()
        // Fecha actual aproximada para que de 1 año
        val fecha = java.time.LocalDate.now().minusYears(1).toString()

        val resultado = viewModel.calcularEdad(fecha)

        assertEquals("1 años", resultado) // O "12 meses" dependiendo de tu lógica exacta
    }
}