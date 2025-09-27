package com.vicherarr.locgps.presentation.vehicles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vicherarr.locgps.presentation.LocalViewModelFactory
import com.vicherarr.locgps.presentation.components.formatAsReadable

@Composable
fun VehicleDetailScreen(
    viewModel: VehicleDetailViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        if (state.isLoadingVehicle && state.vehicle == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Cargando vehículo…", modifier = Modifier.padding(top = 16.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    state.vehicle?.let { vehicle ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(vehicle.plate, style = MaterialTheme.typography.headlineSmall)
                            vehicle.description?.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.bodyLarge)
                            }
                            Text(
                                text = if (vehicle.active) "Estatus: Activo" else "Estatus: Inactivo",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    state.currentLocation?.let { location ->
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Text("Última ubicación", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${location.latitude}, ${location.longitude}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Reportado el ${location.sampledAtUtc.formatAsReadable()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                item {
                    Text("Editar datos del vehículo", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = state.editDescription,
                        onValueChange = viewModel::onEditDescriptionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = { Text("Descripción") }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activo", modifier = Modifier.weight(1f))
                        Switch(checked = state.editActive, onCheckedChange = viewModel::onEditActiveChange)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::updateVehicle,
                        enabled = state.vehicle != null && !state.isUpdatingVehicle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isUpdatingVehicle) {
                            CircularProgressIndicator(modifier = Modifier.height(20.dp))
                        } else {
                            Text("Guardar cambios")
                        }
                    }
                    state.updateMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    state.updateErrorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                item {
                    Text("Historial de ubicaciones", style = MaterialTheme.typography.titleMedium)
                    AssistChipRow(
                        selected = state.range,
                        options = HistoryRange.presets(),
                        onSelected = viewModel::loadHistory
                    )
                }
                when {
                    state.isLoadingHistory -> {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                    state.history?.samples?.isNotEmpty() == true -> {
                        items(state.history!!.samples) { sample ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = sample.sampledAtUtc.formatAsReadable(), fontWeight = FontWeight.SemiBold)
                                Text(text = "Lat: ${sample.latitude}  Lon: ${sample.longitude}")
                                sample.speed?.let { Text("Velocidad: ${it} km/h") }
                                sample.accuracy?.let { Text("Precisión: ±${it} m") }
                            }
                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                        }
                    }
                    else -> {
                        item {
                            Text(
                                text = state.errorMessage ?: "Sin datos en el rango seleccionado",
                                color = if (state.errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssistChipRow(
    selected: HistoryRange,
    options: List<HistoryRange>,
    onSelected: (HistoryRange) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        options.forEach { range ->
            AssistChip(
                onClick = { onSelected(range) },
                label = { Text(range.label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected == range) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                    labelColor = if (selected == range) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}
