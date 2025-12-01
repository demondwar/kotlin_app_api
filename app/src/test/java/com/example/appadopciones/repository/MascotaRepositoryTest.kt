package com.example.appadopciones.repository

import com.example.appadopciones.model.PublicacionModel
import org.junit.Assert.*
import org.junit.Test

class MascotaRepositoryTest {

    @Test
    fun `agregarMascota incrementa el tamano de la lista`() {
        // 1. Given
        val repositorio = MascotaRepository
        val cantidadInicial = repositorio.obtenerMascotas().size

        val nuevaPublicacion = PublicacionModel(
            nombreMascota = "Test Dog",
            especie = "Perro",
            fechaNacimiento = "2023-01-01",
            descripcion = "Test descripcion",
            telefonoContacto = "12345678"
        )

        // 2. When: Agregamos una mascota (sin foto)
        repositorio.agregarMascota(nuevaPublicacion, null)

        // 3. Then
        val cantidadFinal = repositorio.obtenerMascotas().size
        assertEquals(cantidadInicial + 1, cantidadFinal)

        // Verificamos que se guardó correctamente
        val ultimaMascota = repositorio.obtenerMascotas().last()
        assertEquals("Test Dog", ultimaMascota.nombre)
    }

    @Test
    fun `eliminarMascota reduce la lista`() {
        // 1. Given: Agregamos una mascota específica para borrar
        val repositorio = MascotaRepository
        val nuevaPublicacion = PublicacionModel(nombreMascota = "Borrarme")
        repositorio.agregarMascota(nuevaPublicacion, null)

        val mascotaABorrar = repositorio.obtenerMascotas().last()
        val cantidadAntes = repositorio.obtenerMascotas().size

        // 2. When
        repositorio.eliminarMascota(mascotaABorrar.id)

        // 3. Then
        val cantidadDespues = repositorio.obtenerMascotas().size
        assertEquals(cantidadAntes - 1, cantidadDespues)

        // Verificamos que ya no exista
        val existe = repositorio.obtenerMascotas().any { it.id == mascotaABorrar.id }
        assertFalse(existe)
    }
}