package com.example.medicitas.data.remote.api

import com.example.medicitas.data.remote.dto.ApiResponseDto
import com.example.medicitas.data.remote.dto.AtencionRequestDto
import com.example.medicitas.data.remote.dto.CitaDto
import com.example.medicitas.data.remote.dto.ListaDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CitaApiService {
    // Devuelve solo las citas del médico logueado
    @GET("citas")
    suspend fun getCitas(): ApiResponseDto<ListaDataDto<CitaDto>>

    @GET("citas/{id}")
    suspend fun getCita(@Path("id") id: String): ApiResponseDto<CitaDto>

    @POST("citas/{id}/atencion")
    suspend fun registrarAtencion(@Path("id") id: String, @Body atencion: AtencionRequestDto): ApiResponseDto<CitaDto>
}
