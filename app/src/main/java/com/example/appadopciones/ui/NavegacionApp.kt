package com.example.appadopciones.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appadopciones.viewModel.DetalleMascotaViewModel
import com.example.appadopciones.viewModel.ListaMascotasViewModel
import com.example.appadopciones.viewModel.PublicarViewModel

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val listaMascotasViewModel: ListaMascotasViewModel = viewModel()
    val publicarViewModel: PublicarViewModel = viewModel()

    NavHost(navController = navController, startDestination = "lista_mascotas") {

        composable("lista_mascotas") {
            ListaMascotasView(navController = navController, viewModel = listaMascotasViewModel)
        }

        composable("detalle_mascota/{mascotaId}") { backStackEntry ->
            // ... (Igual que antes)
            val id = backStackEntry.arguments?.getString("mascotaId")?.toIntOrNull()
            if (id != null) {
                DetalleMascotaView(navController, id)
            }
        }

        // --- RUTA MODIFICADA ---
        // Ahora aceptamos un parametro opcional ?mascotaId={id}
        composable(
            route = "publicar_mascota?mascotaId={mascotaId}",
            arguments = listOf(
                navArgument("mascotaId") {
                    type = NavType.IntType
                    defaultValue = -1 // -1 indica que es nuevo
                }
            )
        ) { backStackEntry ->
            val mascotaId = backStackEntry.arguments?.getInt("mascotaId") ?: -1

            // Pasamos el ID a la vista
            PublicarView(
                viewModel = publicarViewModel,
                navController = navController,
                mascotaId = mascotaId
            )
        }
    }
}