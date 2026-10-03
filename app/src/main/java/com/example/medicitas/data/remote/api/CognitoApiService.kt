package com.example.medicitas.data.remote.api

import com.example.medicitas.data.remote.dto.InitiateAuthRequestDto
import com.example.medicitas.data.remote.dto.InitiateAuthResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Login contra Amazon Cognito sin SDK: es una llamada HTTPS pública (no requiere firma de AWS)
interface CognitoApiService {
    @Headers(
        "Content-Type: application/x-amz-json-1.1",
        "X-Amz-Target: AWSCognitoIdentityProviderService.InitiateAuth"
    )
    @POST("/")
    suspend fun initiateAuth(@Body request: InitiateAuthRequestDto): InitiateAuthResponseDto
}
