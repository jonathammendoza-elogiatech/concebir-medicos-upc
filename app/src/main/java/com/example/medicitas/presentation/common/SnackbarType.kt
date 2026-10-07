package com.example.medicitas.presentation.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

enum class SnackbarType { SUCCESS, ERROR, WARNING, INFO }

// El tipo viaja con el mensaje para que cada snackbar se pinte con su propio color
data class TypedSnackbarVisuals(
    override val message: String,
    val tipo: SnackbarType,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals
