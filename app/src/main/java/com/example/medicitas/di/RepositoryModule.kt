package com.example.medicitas.di

import com.example.medicitas.data.local.SesionPreferences
import com.example.medicitas.data.mock.SysmedicalMockDataSource
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

// Mientras no exista el servicio REST (E6), los repositorios leen del mock de Sysmedical
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(dataSource: SysmedicalMockDataSource, sesionPreferences: SesionPreferences): AuthRepository {
        return AuthRepositoryImpl(dataSource, sesionPreferences)
    }

    @Provides
    @Singleton
    fun provideMedicoRepository(dataSource: SysmedicalMockDataSource): MedicoRepository {
        return MedicoRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideCitaRepository(dataSource: SysmedicalMockDataSource): CitaRepository {
        return CitaRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun providePacienteRepository(dataSource: SysmedicalMockDataSource): PacienteRepository {
        return PacienteRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideResultadoRepository(dataSource: SysmedicalMockDataSource): ResultadoRepository {
        return ResultadoRepositoryImpl(dataSource)
    }
}
