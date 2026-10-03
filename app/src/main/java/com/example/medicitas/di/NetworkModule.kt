package com.example.medicitas.di

import com.example.medicitas.data.remote.ApiConfig
import com.example.medicitas.data.remote.AuthInterceptor
import com.example.medicitas.data.remote.api.CitaApiService
import com.example.medicitas.data.remote.api.CognitoApiService
import com.example.medicitas.data.remote.api.MedicoApiService
import com.example.medicitas.data.remote.api.PacienteApiService
import com.example.medicitas.data.remote.api.ResultadoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
            })
            .build()
    }

    @Provides
    @Singleton
    @ConcebirRetrofit
    fun provideConcebirRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @CognitoRetrofit
    fun provideCognitoRetrofit(): Retrofit {
        // Cliente propio: sin token y sin loguear el body (lleva la contraseña y los tokens)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
        return Retrofit.Builder()
            .baseUrl(ApiConfig.COGNITO_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCognitoApiService(@CognitoRetrofit retrofit: Retrofit): CognitoApiService {
        return retrofit.create(CognitoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMedicoApiService(@ConcebirRetrofit retrofit: Retrofit): MedicoApiService {
        return retrofit.create(MedicoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCitaApiService(@ConcebirRetrofit retrofit: Retrofit): CitaApiService {
        return retrofit.create(CitaApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePacienteApiService(@ConcebirRetrofit retrofit: Retrofit): PacienteApiService {
        return retrofit.create(PacienteApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideResultadoApiService(@ConcebirRetrofit retrofit: Retrofit): ResultadoApiService {
        return retrofit.create(ResultadoApiService::class.java)
    }
}
