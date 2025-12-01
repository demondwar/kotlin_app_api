package com.example.appadopciones.repository

import com.example.appadopciones.model.Mascota
import com.example.appadopciones.model.PublicacionModel
import com.example.appadopciones.network.MascotaDto
import com.example.appadopciones.network.PublicacionDto
import com.example.appadopciones.network.RetrofitClient
import com.example.appadopciones.repository.ID_USUARIO_ACTUAL

object RemoteMascotaRepository {
    private val api = RetrofitClient.apiService

    private fun dtoToModel(dto: MascotaDto): Mascota {
        return Mascota(
            id = dto.id,
            nombre = dto.nombre,
            especie = dto.especie,
            fechaNacimiento = dto.fechaNacimiento ?: "2000-01-01",
            urlImagen = dto.urlImagen,
            estaEnAdopcion = dto.estaEnAdopcion,
            idUsuarioPublicador = dto.idUsuarioPublicador,
            telefonoContacto = dto.telefonoContacto ?: "",
            descripcion = dto.descripcion ?: ""
        )
    }

    private fun publicacionModelToDto(publicacion: PublicacionModel, urlImagen: String?): PublicacionDto {
        return PublicacionDto(
            nombre = publicacion.nombreMascota,
            especie = publicacion.especie,
            fechaNacimiento = publicacion.fechaNacimiento.ifEmpty { null },
            descripcion = publicacion.descripcion.ifEmpty { null },
            nombreContacto = publicacion.nombreContacto.ifEmpty { null },
            telefonoContacto = publicacion.telefonoContacto.ifEmpty { null },
            urlImagen = urlImagen,
            idUsuarioPublicador = ID_USUARIO_ACTUAL
        )
    }

    suspend fun fetchMascotas(): List<Mascota> {
        val listaDto = api.getMascotas()
        return listaDto.map { dtoToModel(it) }
    }

    suspend fun crearMascota(publicacion: PublicacionModel, urlImagen: String?): Mascota {
        val dto = publicacionModelToDto(publicacion, urlImagen)
        val creado = api.crearMascota(dto)
        return dtoToModel(creado)
    }

    suspend fun actualizarMascota(id: Int, publicacion: PublicacionModel, urlImagen: String?): Mascota {
        val dto = publicacionModelToDto(publicacion, urlImagen)
        val actualizado = api.actualizarMascota(id, dto)
        return dtoToModel(actualizado)
    }

    suspend fun eliminarMascota(id: Int) {
        api.eliminarMascota(id)
    }
}