package com.example.appadopciones.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

private fun crearUriTemporal(contexto: android.content.Context): Uri {
    val directorio = File(contexto.cacheDir, "imagenes_temp")
    directorio.mkdirs()
    val archivo = File(directorio, "foto_mascota_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        contexto,
        "${contexto.packageName}.provider",
        archivo
    )
}

@Composable
fun BotonTomarFoto(
    onSuccess: (Uri?) -> Unit
) {
    val contexto = LocalContext.current
    val uriFoto = androidx.compose.runtime.remember { crearUriTemporal(contexto) }

    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { esExitoso ->
        if (esExitoso) {
            onSuccess(uriFoto)
        } else {
            onSuccess(null)
        }
    }

    val lanzadorPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            lanzadorCamara.launch(uriFoto)
        } else {
        }
    }

    Button(onClick = {
        lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
    }) {
        Icon(Icons.Filled.CameraAlt, contentDescription = "Tomar Foto")
        Spacer(Modifier.width(8.dp))
        Text("Agregar Foto de la Mascota")
    }
}

@Composable
fun BotonObtenerUbicacion(alUbicacionObtenida: (Pair<Double, Double>?) -> Unit) {
    val lanzadorPermisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            alUbicacionObtenida(Pair(-69.67, 27.72))
        } else {
            alUbicacionObtenida(null)
        }
    }

    Button(onClick = {
        lanzadorPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }) {
        Text("Usar Ubicación Actual")
    }
}