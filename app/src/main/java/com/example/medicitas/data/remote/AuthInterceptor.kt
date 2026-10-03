package com.example.medicitas.data.remote

import com.example.medicitas.data.local.SesionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

// Agrega el token de Cognito a cada llamada; API Gateway lo valida antes de llegar a la Lambda
class AuthInterceptor @Inject constructor(
    private val sesionManager: SesionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sesionManager.accessToken ?: return chain.proceed(chain.request())
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
