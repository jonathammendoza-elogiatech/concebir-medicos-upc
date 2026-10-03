package com.example.medicitas.data.remote.api

import com.example.medicitas.data.remote.dto.ApiResponseDto
import com.example.medicitas.data.remote.dto.ListaDataDto
import com.example.medicitas.data.remote.dto.PacienteDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PacienteApiService {
    @GET("pacientes")
    suspend fun getPacientes(): ApiResponseDto<ListaDataDto<PacienteDto>>

    @GET("pacientes/{id}")
    suspend fun getPaciente(@Path("id") id: String): ApiResponseDto<PacienteDto>
}
