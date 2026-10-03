package com.example.medicitas.data.remote.dto

import com.google.gson.annotations.SerializedName

// Cognito usa nombres en PascalCase (API InitiateAuth)
data class InitiateAuthRequestDto(
    @SerializedName("AuthFlow") val authFlow: String,
    @SerializedName("ClientId") val clientId: String,
    @SerializedName("AuthParameters") val authParameters: Map<String, String>
)

data class InitiateAuthResponseDto(
    @SerializedName("AuthenticationResult") val authenticationResult: AuthenticationResultDto?,
    // Viene en lugar de los tokens cuando Cognito pide un paso extra (p. ej. NEW_PASSWORD_REQUIRED)
    @SerializedName("ChallengeName") val challengeName: String?
)

data class AuthenticationResultDto(
    @SerializedName("AccessToken") val accessToken: String?,
    @SerializedName("IdToken") val idToken: String?,
    // Solo llega en el login con contraseña, no al renovar
    @SerializedName("RefreshToken") val refreshToken: String?,
    @SerializedName("ExpiresIn") val expiresIn: Int?
)
