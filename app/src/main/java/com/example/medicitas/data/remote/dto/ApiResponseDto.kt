package com.example.medicitas.data.remote.dto

data class ApiResponseDto<T>(
    val success: Boolean,
    val data: T,
    val timestamp: String,
)

// Igual que en clase: si el API responde success = false, se trata como error
fun <T> ApiResponseDto<T>.datosOError(): T =
    if (success) data else throw Exception("Error en la respuesta del servidor")

// Listados del API: { "items": [...] }
data class ListaDataDto<T>(
    val items: List<T>,
)
