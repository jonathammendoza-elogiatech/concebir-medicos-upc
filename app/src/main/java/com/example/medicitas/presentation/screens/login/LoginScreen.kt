package com.example.medicitas.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.presentation.common.TarjetaClinica
import com.example.medicitas.presentation.common.versionApp
import com.example.medicitas.ui.theme.EstadoAlertaFondo
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.TextoSecundario

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { efecto ->
            when (efecto) {
                LoginEffect.SesionIniciada -> onLoginExitoso()
            }
        }
    }

    LoginContent(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
private fun LoginContent(uiState: LoginUiState, onEvent: (LoginEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Insignia(icono = Icons.Outlined.Lock, texto = "CONEXIÓN CIFRADA")
        }
        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.HealthAndSafety, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Concebir Médicos", style = MaterialTheme.typography.headlineLarge)
        Text("Acceso para personal médico", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        Spacer(Modifier.height(12.dp))
        Insignia(icono = Icons.Outlined.VerifiedUser, texto = "Red asistencial SERPROSA")
        Spacer(Modifier.height(24.dp))

        TarjetaClinica(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
            if (uiState.error != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EstadoAlertaFondo, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.error.orEmpty(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = uiState.cmp,
                onValueChange = { onEvent(LoginEvent.CmpChange(it)) },
                label = { Text("Código CMP") },
                supportingText = { Text("Colegio Médico del Perú") },
                placeholder = { Text("Ej. 012345") },
                leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                singleLine = true,
                isError = uiState.error != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                colors = coloresCampo(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.contrasena,
                onValueChange = { onEvent(LoginEvent.ContrasenaChange(it)) },
                label = { Text("Contraseña") },
                placeholder = { Text("Ingresa tu clave médica") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { onEvent(LoginEvent.AlternarVisibilidad) }) {
                        Icon(
                            if (uiState.mostrarContrasena) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (uiState.mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                singleLine = true,
                isError = uiState.error != null,
                visualTransformation = if (uiState.mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onEvent(LoginEvent.IniciarSesion) }),
                colors = coloresCampo(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = uiState.recordarUsuario, onCheckedChange = { onEvent(LoginEvent.RecordarChange(it)) })
                Text("Recordar mi usuario", style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Spacer(Modifier.weight(1f))
            }
            TextButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
                Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { onEvent(LoginEvent.IniciarSesion) },
                enabled = uiState.puedeIngresar,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (uiState.cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Iniciar sesión", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
            if (uiState.biometriaDisponible) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                    Text("o", style = MaterialTheme.typography.bodySmall, color = TextoSecundario, modifier = Modifier.padding(horizontal = 12.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                }
                FilledTonalButton(
                    onClick = { onEvent(LoginEvent.AbrirBiometria) },
                    enabled = !uiState.cargando,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Filled.Fingerprint, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ingresar con biometría", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            val avisoBiometria = if (uiState.biometriaDisponible) {
                "Biometría activa en este dispositivo · Clave como respaldo"
            } else {
                "Marca \"Recordar mi usuario\" para ingresar con biometría la próxima vez"
            }
            Text(avisoBiometria, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
        Spacer(Modifier.height(32.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(12.dp))
        Text(
            "SERPROSA · Clínica Concebir\nv${versionApp()} · Acceso seguro",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
    }

    if (uiState.mostrarBiometria) {
        AlertDialog(
            onDismissRequest = { onEvent(LoginEvent.CerrarBiometria) },
            icon = { Icon(Icons.Filled.Fingerprint, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Ingresar con biometría") },
            text = { Text("Toca el sensor para validar tu identidad.", textAlign = TextAlign.Center) },
            confirmButton = { TextButton(onClick = { onEvent(LoginEvent.ConfirmarBiometria) }) { Text("Tocar sensor") } },
            dismissButton = { TextButton(onClick = { onEvent(LoginEvent.CerrarBiometria) }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Insignia(icono: ImageVector, texto: String) {
    Row(
        modifier = Modifier
            .background(PetroleoClaro, CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
        Text(texto, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor = TextoSecundario,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary
)
