package com.example.medicitas.data.local

import javax.inject.Inject
import javax.inject.Singleton

// El access token vive solo en memoria: al cerrar la app hay que volver a ingresar
@Singleton
class SesionManager @Inject constructor() {

    @Volatile
    var accessToken: String? = null
        private set

    fun iniciar(accessToken: String) {
        this.accessToken = accessToken
    }

    fun cerrar() {
        accessToken = null
    }
}
