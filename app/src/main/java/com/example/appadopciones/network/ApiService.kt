package com.example.appadopciones.network
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    // GET /mascotas -> devuelve lista
    @GET("mascotas")
    suspend fun getMascotas(): List<MascotaDto>

    // POST /mascotas -> crea nueva
    @POST("mascotas")
    suspend fun crearMascota(@Body body: PublicacionDto): MascotaDto

    // --- NUEVO: PUT para editar ---
    // Usamos {id} para decirle a la API cual mascota específica modificar
    @PUT("mascotas/{id}")
    suspend fun actualizarMascota(
        @Path("id") id: Int,
        @Body body: PublicacionDto
    ): MascotaDto

    // --- NUEVO: DELETE para eliminar ---
    @DELETE("mascotas/{id}")
    suspend fun eliminarMascota(@Path("id") id: Int): MascotaDto
}