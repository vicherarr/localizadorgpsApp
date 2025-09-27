package com.vicherarr.locgps.presentation.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vicherarr.locgps.presentation.LocalViewModelFactory

@Composable
fun ManagementScreen(
    viewModel: ManagementViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            state = listState
        ) {
            item {
                SectionCard(title = "Registrar nuevo vehículo") {
                    OutlinedTextField(
                        value = state.vehicleForm.plate,
                        onValueChange = viewModel::onVehiclePlateChange,
                        label = { Text("Placa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.vehicleForm.description,
                        onValueChange = viewModel::onVehicleDescriptionChange,
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = viewModel::submitVehicle,
                        enabled = !state.vehicleForm.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.vehicleForm.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.height(20.dp))
                        } else {
                            Text("Crear vehículo")
                        }
                    }
                    FeedbackMessages(
                        success = state.vehicleForm.successMessage,
                        error = state.vehicleForm.errorMessage,
                        onClear = {
                            if (state.vehicleForm.successMessage != null) {
                                viewModel.onVehiclePlateChange("")
                                viewModel.onVehicleDescriptionChange("")
                            }
                        }
                    )
                }
            }

            item {
                SectionCard(title = "Registrar dispositivo") {
                    var showPassword by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = state.deviceForm.username,
                        onValueChange = viewModel::onDeviceUsernameChange,
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.deviceForm.password,
                        onValueChange = viewModel::onDevicePasswordChange,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { showPassword = !showPassword }) {
                                Text(if (showPassword) "Ocultar" else "Mostrar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.deviceForm.uniqueIdentifier,
                        onValueChange = viewModel::onDeviceIdentifierChange,
                        label = { Text("Identificador único") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.deviceForm.vehicleId,
                        onValueChange = viewModel::onDeviceVehicleIdChange,
                        label = { Text("ID de vehículo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.deviceForm.description,
                        onValueChange = viewModel::onDeviceDescriptionChange,
                        label = { Text("Descripción del dispositivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = viewModel::registerDevice,
                        enabled = !state.deviceForm.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.deviceForm.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.height(20.dp))
                        } else {
                            Text("Registrar dispositivo")
                        }
                    }
                    FeedbackMessages(
                        success = state.deviceForm.successMessage,
                        error = state.deviceForm.errorMessage,
                        onClear = {
                            if (state.deviceForm.successMessage != null) {
                                viewModel.onDeviceUsernameChange("")
                                viewModel.onDevicePasswordChange("")
                                viewModel.onDeviceIdentifierChange("")
                                viewModel.onDeviceVehicleIdChange("")
                                viewModel.onDeviceDescriptionChange("")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FeedbackMessages(success: String?, error: String?, onClear: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        success?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp)
            )
            LaunchedEffect(it) { onClear() }
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
