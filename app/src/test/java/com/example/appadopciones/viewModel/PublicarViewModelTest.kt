package com.example.appadopciones.viewModel

import com.example.appadopciones.MainDispatcherRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class PublicarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `calcularEdad retorna formato correcto en años`() {
        // 1. Given (Dado): Un ViewModel y una fecha de hace 2 años
        val viewModel = PublicarViewModel()
        val fechaHaceDosAnos = LocalDate.now().minusYears(2).toString()

        // Simulamos que el usuario ingresó esa fecha en el modelo
        viewModel.onFechaNacimientoChange(fechaHaceDosAnos)

        // 2. When (Cuando): Ejecutamos el cálculo
        val resultado = viewModel.calcularEdad()

        // 3. Then (Entonces): Esperamos que diga "2 años"
        assertEquals("2 años", resultado)
    }

    @Test
    fun `verificarDescripcion falla si tiene menos de 10 caracteres`() {
        // 1. Given
        val viewModel = PublicarViewModel()
        viewModel.onDescripcionChange("Hola") // Muy corta

        // 2. When
        val esValido = viewModel.verificarDescripcion()

        // 3. Then
        assertFalse("Debería fallar con texto corto", esValido)
        assertEquals("Mínimo 10 caracteres", viewModel.mensajesError.errorDescripcion)
    }

    @Test
    fun `verificarDescripcion pasa si tiene mas de 10 caracteres`() {
        // 1. Given
        val viewModel = PublicarViewModel()
        viewModel.onDescripcionChange("Esta es una descripción válida y suficientemente larga")

        // 2. When
        val esValido = viewModel.verificarDescripcion()

        // 3. Then
        assertTrue("Debería pasar con texto largo", esValido)
        assertEquals("", viewModel.mensajesError.errorDescripcion)
    }

    @Test
    fun `verificarNombreMascota falla si esta vacio`() {
        val viewModel = PublicarViewModel()
        viewModel.onNombreMascotaChange("") // Vacío

        val esValido = viewModel.verificarNombreMascota()

        assertFalse(esValido)
        assertEquals("Requerido", viewModel.mensajesError.errorNombreMascota)
    }
    @Test
    fun `verificarTelefonoContacto falla con letras o muy corto`() {
        val viewModel = PublicarViewModel()

        // Caso 1: Letras
        viewModel.onTelefonoContactoChange("holamundo")
        assertFalse("No debería aceptar letras", viewModel.verificarTelefonoContacto())

        // Caso 2: Muy corto
        viewModel.onTelefonoContactoChange("123")
        assertFalse("No debería aceptar números muy cortos", viewModel.verificarTelefonoContacto())
        }

    @Test
    fun `verificarTelefonoContacto pasa con numeros validos`() {
        val viewModel = PublicarViewModel()

        viewModel.onTelefonoContactoChange("912345678") // 9 dígitos
        assertTrue(viewModel.verificarTelefonoContacto())
        assertEquals("", viewModel.mensajesError.errorTelefonoContacto)

    }
}