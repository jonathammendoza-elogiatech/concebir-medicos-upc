package com.example.medicitas.domain.repository

interface AuthRepository {
    suspend fun login(cmp: String, contrasena: String, recordarUsuario: Boolean): Result<Unit>
    suspend fun loginBiometrico(): Result<Unit>
    fun getCmpRecordado(): String?
    suspend fun logout(): Result<Unit>
}
