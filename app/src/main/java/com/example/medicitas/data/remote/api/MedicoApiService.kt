package com.example.medicitas.data.remote.api

import com.example.medicitas.data.remote.dto.ApiResponseDto
import com.example.medicitas.data.remote.dto.MedicoDto
import com.example.medicitas.data.remote.dto.PerfilRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface MedicoApiService {
    // "me" = el médico del token de Cognito
    @GET("medicos/me")
    suspend fun getPerfil(): ApiResponseDto<MedicoDto>

    @PATCH("medicos/me")
    suspend fun actualizarPerfil(@Body cambios: PerfilRequestDto): ApiResponseDto<MedicoDto>
}
