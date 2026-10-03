package com.example.medicitas.data.remote

// Despliegue en AWS (ver README de concebir-medicos-api). No son secretos: el Client ID de
// Cognito es público y cada llamada al API exige un token válido.
object ApiConfig {
    const val BASE_URL = "https://0pn0osiqr1.execute-api.us-east-1.amazonaws.com/"
    const val COGNITO_URL = "https://cognito-idp.us-east-1.amazonaws.com/"
    const val COGNITO_CLIENT_ID = "2b3m67nub9tnakm6ifiac16mje"
}
