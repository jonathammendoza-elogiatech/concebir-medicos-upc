package com.example.medicitas.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SesionPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE)

    var cmpRecordado: String?
        get() = prefs.getString(KEY_CMP, null)
        set(value) = prefs.edit { if (value == null) remove(KEY_CMP) else putString(KEY_CMP, value) }

    // Cifrado con SesionCipher; permite ingresar con biometría sin volver a escribir la contraseña
    var refreshTokenCifrado: String?
        get() = prefs.getString(KEY_REFRESH, null)
        set(value) = prefs.edit { if (value == null) remove(KEY_REFRESH) else putString(KEY_REFRESH, value) }

    private companion object {
        const val NOMBRE = "sesion_medicitas"
        const val KEY_CMP = "cmp_recordado"
        const val KEY_REFRESH = "refresh_token_cifrado"
    }
}
