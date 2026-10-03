package com.example.medicitas.data.remote.api

import com.example.medicitas.data.remote.dto.ApiResponseDto
import com.example.medicitas.data.remote.dto.ListaDataDto
import com.example.medicitas.data.remote.dto.ResultadoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ResultadoApiService {
    @GET("resultados")
    suspend fun getResultados(@Query("pacienteId") pacienteId: String): ApiResponseDto<ListaDataDto<ResultadoDto>>
}
