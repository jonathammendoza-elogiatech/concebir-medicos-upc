package com.example.medicitas.data.repository

import com.example.medicitas.data.local.SesionPreferences
import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: SysmedicalMockDataSource,
    private val sesionPreferences: SesionPreferences
) : AuthRepository {

    override suspend fun login(cmp: String, contrasena: String, recordarUsuario: Boolean): Result<Unit> = runCatching {
        dataSource.simularLatencia()
        if (!dataSource.credencialesValidas(cmp, contrasena)) {
            throw CredencialesInvalidasException()
        }
        sesionPreferences.cmpRecordado = if (recordarUsuario) cmp else null
    }

    override suspend fun loginBiometrico(): Result<Unit> = runCatching {
        dataSource.simularLatencia()
    }

    override fun getCmpRecordado(): String? = sesionPreferences.cmpRecordado

    override suspend fun logout(): Result<Unit> = runCatching { }
}

class CredencialesInvalidasException : Exception("CMP o contraseña incorrectos")
