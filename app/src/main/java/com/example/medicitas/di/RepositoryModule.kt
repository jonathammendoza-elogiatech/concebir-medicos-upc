package com.example.medicitas.di

import com.example.medicitas.data.local.SesionCipher
import com.example.medicitas.data.local.SesionManager
import com.example.medicitas.data.local.SesionPreferences
import com.example.medicitas.data.remote.api.CitaApiService
import com.example.medicitas.data.remote.api.CognitoApiService
import com.example.medicitas.data.remote.api.MedicoApiService
import com.example.medicitas.data.remote.api.PacienteApiService
import com.example.medicitas.data.remote.api.ResultadoApiService
import com.example.medicitas.data.repository.AuthRepositoryImpl
import com.example.medicitas.data.repository.CitaRepositoryImpl
import com.example.medicitas.data.repository.MedicoRepositoryImpl
import com.example.medicitas.data.repository.PacienteRepositoryImpl
import com.example.medicitas.data.repository.ResultadoRepositoryImpl
import com.example.medicitas.domain.repository.AuthRepository
import com.example.medicitas.domain.repository.CitaRepository
import com.example.medicitas.domain.repository.MedicoRepository
import com.example.medicitas.domain.repository.PacienteRepository
import com.example.medicitas.domain.repository.ResultadoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Los repositorios consumen el servicio REST (E6): API Gateway + Lambda + DynamoDB, login con Cognito
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        cognitoApiService: CognitoApiService,
        sesionManager: SesionManager,
        sesionPreferences: SesionPreferences,
        sesionCipher: SesionCipher
    ): AuthRepository {
        return AuthRepositoryImpl(cognitoApiService, sesionManager, sesionPreferences, sesionCipher)
    }

    @Provides
    @Singleton
    fun provideMedicoRepository(medicoApiService: MedicoApiService): MedicoRepository {
        return MedicoRepositoryImpl(medicoApiService)
    }

    @Provides
    @Singleton
    fun provideCitaRepository(citaApiService: CitaApiService): CitaRepository {
        return CitaRepositoryImpl(citaApiService)
    }

    @Provides
    @Singleton
    fun providePacienteRepository(pacienteApiService: PacienteApiService): PacienteRepository {
        return PacienteRepositoryImpl(pacienteApiService)
    }

    @Provides
    @Singleton
    fun provideResultadoRepository(resultadoApiService: ResultadoApiService): ResultadoRepository {
        return ResultadoRepositoryImpl(resultadoApiService)
    }
}
