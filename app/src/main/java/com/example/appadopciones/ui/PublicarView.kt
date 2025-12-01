package com.example.appadopciones.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appadopciones.viewModel.PublicarViewModel
import coil.compose.rememberAsyncImagePainter
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarView(
    viewModel: PublicarViewModel,
    navController: NavController,
    mascotaId: Int = -1
) {
    val esModoEdicion = mascotaId != -1
    val mostrarDialogoFecha = remember { mutableStateOf(false) }

    val fechaInicialMillis = try {
        if (viewModel.publicacion.fechaNacimiento.isNotBlank()) {
            LocalDate.parse(viewModel.publicacion.fechaNacimiento, DateTimeFormatter.ISO_DATE)
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        } else {
            LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000
        }
    } catch (e: Exception) {
        LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000
    }
    val estadoDatePicker = rememberDatePickerState(
        initialSelectedDateMillis = fechaInicialMillis
    )


    LaunchedEffect(mascotaId) {
        viewModel.resetearEstado()
        if (esModoEdicion) {
            viewModel.cargarDatosParaEdicion(mascotaId)
        }
    }

    LaunchedEffect(viewModel.publicacionExitosa) {
        if (viewModel.publicacionExitosa) {
            navController.popBackStack()
            viewModel.resetearEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esModoEdicion) "Editar Mascota" else "Publicar Mascota") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetearEstado()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- FOTO ---
            if (viewModel.uriFotoSeleccionada != null) {
                Image(
                    painter = rememberAsyncImagePainter(viewModel.uriFotoSeleccionada),
                    contentDescription = "Nueva foto",
                    modifier = Modifier.size(150.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else if (esModoEdicion) {
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Foto actual conservada.\n(Toma una nueva para cambiarla)",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            BotonTomarFoto(
                onSuccess = { uri -> viewModel.onUriFotoSeleccionada(uri) }
            )

            if (!viewModel.verificarFoto() && viewModel.publicacion.nombreMascota.isNotEmpty()) {
                Text(
                    text = "Debes tomar una foto de la mascota",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // --- CAMPOS ---

            CampoPublicacion(
                valor = viewModel.publicacion.nombreMascota,
                etiqueta = "Nombre de la Mascota",
                ejemplo = "Ej. Firulais",
                errorMensaje = viewModel.mensajesError.errorNombreMascota,
                onValueChange = {
                    viewModel.onNombreMascotaChange(it)
                    viewModel.verificarNombreMascota()
                }
            )

            FechaNacimientoCampo(
                valor = viewModel.publicacion.fechaNacimiento,
                edadCalculada = viewModel.calcularEdad(),
                errorMensaje = viewModel.mensajesError.errorFechaNacimiento,
                alHacerClick = { mostrarDialogoFecha.value = true }
            )

            CampoPublicacion(
                valor = viewModel.publicacion.especie,
                etiqueta = "Especie",
                ejemplo = "Ej. Perro, Gato",
                errorMensaje = viewModel.mensajesError.errorEspecie,
                onValueChange = {
                    viewModel.onEspecieChange(it)
                    viewModel.verificarEspecie()
                }
            )

            CampoPublicacion(
                valor = viewModel.publicacion.descripcion,
                etiqueta = "Descripción de la Mascota",
                ejemplo = "Háblanos sobre su carácter...",
                errorMensaje = viewModel.mensajesError.errorDescripcion,
                onValueChange = {
                    viewModel.onDescripcionChange(it)
                    viewModel.verificarDescripcion()
                },
                maxLineas = 4,
                imeAction = ImeAction.Default
            )

            CampoPublicacion(
                valor = viewModel.publicacion.telefonoContacto,
                etiqueta = "Teléfono de Contacto",
                ejemplo = "Ej. 912345678",
                errorMensaje = viewModel.mensajesError.errorTelefonoContacto,
                onValueChange = {
                    viewModel.onTelefonoContactoChange(it)
                    viewModel.verificarTelefonoContacto()
                },
                keyboardType = KeyboardType.Phone
            )

            // Espacio y Botón
            Spacer(modifier = Modifier.height(8.dp))

            // 👇 INTEGRACIÓN DEL MENSAJE DE ERROR GENERAL
            if (viewModel.mensajesError.errorGeneral.isNotEmpty()) {
                Text(
                    text = viewModel.mensajesError.errorGeneral,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { viewModel.publicarMascota() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esModoEdicion) "Guardar Cambios" else "Publicar en Adopción")
            }

            AnimatedVisibility(
                visible = viewModel.publicacionExitosa,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Text(
                        text = if (esModoEdicion) "¡Cambios Guardados!" else "¡Publicación Exitosa!",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    // DIÁLOGO DEL CALENDARIO
    if (mostrarDialogoFecha.value) {
        DatePickerDialog(
            onDismissRequest = { mostrarDialogoFecha.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fechaSeleccionada = estadoDatePicker.selectedDateMillis?.let {
                            LocalDate.ofEpochDay(it / (1000 * 60 * 60 * 24))
                        }
                        if (fechaSeleccionada != null) {
                            viewModel.onFechaNacimientoChange(fechaSeleccionada.format(DateTimeFormatter.ISO_DATE))
                            viewModel.verificarFechaNacimiento()
                        }
                        mostrarDialogoFecha.value = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoFecha.value = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoDatePicker)
        }
    }
}

// ----------------------------------------------------
// Componentes Auxiliares
// ----------------------------------------------------

@Composable
fun FechaNacimientoCampo(
    valor: String,
    edadCalculada: String,
    errorMensaje: String,
    alHacerClick: () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { /* Desactivado, se usa el botón */ },
        label = { Text("Fecha de Nacimiento (AAAA-MM-DD)") },
        placeholder = { Text(edadCalculada) },
        readOnly = true,
        isError = errorMensaje.isNotEmpty(),
        trailingIcon = {
            IconButton(onClick = alHacerClick) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Seleccionar fecha")
            }
        },
        supportingText = {
            if (errorMensaje.isNotEmpty()) {
                Text(text = errorMensaje, color = MaterialTheme.colorScheme.error)
            } else {
                Text(text = "Edad aproximada: $edadCalculada", style = MaterialTheme.typography.bodySmall)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CampoPublicacion(
    valor: String,
    etiqueta: String,
    ejemplo: String,
    errorMensaje: String,
    onValueChange: (String) -> Unit,
    maxLineas: Int = 1,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        placeholder = { Text(ejemplo, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        isError = errorMensaje.isNotEmpty(),
        supportingText = {
            if (errorMensaje.isNotEmpty()) {
                Text(text = errorMensaje, color = MaterialTheme.colorScheme.error)
            } else {
                Text(text = "Formato: $ejemplo", style = MaterialTheme.typography.bodySmall)
            }
        },
        trailingIcon = {
            if (errorMensaje.isNotEmpty()) {
                Icon(Icons.Filled.Warning, "Error", tint = MaterialTheme.colorScheme.error)
            } else if (valor.isNotEmpty()) {
                Icon(Icons.Filled.Done, "Válido", tint = MaterialTheme.colorScheme.primary)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        maxLines = maxLineas,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        singleLine = maxLineas == 1
    )
}