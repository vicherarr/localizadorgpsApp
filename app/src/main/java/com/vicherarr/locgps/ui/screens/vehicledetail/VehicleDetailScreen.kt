package com.vicherarr.locgps.ui.screens.vehicledetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.vicherarr.locgps.domain.model.VehicleLocation
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailRoute(
    uiStateFlow: StateFlow<VehicleDetailUiState>,
    onRefresh: () -> Unit,
    onRefreshHistory: () -> Unit,
    onRefreshLocation: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(uiState.latestLocation) {
        uiState.latestLocation?.let { location ->
            val target = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(CameraPosition.fromLatLngZoom(target, 15f))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.vehicle?.plate ?: "Vehículo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar todo")
                    }
                    IconButton(onClick = onRefreshLocation) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Ubicación")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            uiState.errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            VehicleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                location = uiState.latestLocation,
                cameraPositionState = cameraPositionState
            )
            VehicleInfoPanel(uiState)
            uiState.history?.let { history ->
                HistorySection(history.points, onRefreshHistory)
            }
        }
    }
}

@Composable
private fun VehicleMap(
    modifier: Modifier = Modifier,
    location: VehicleLocation?,
    cameraPositionState: CameraPositionState
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (location != null) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = true),
                cameraPositionState = cameraPositionState
            ) {
                val coordinate = LatLng(location.latitude, location.longitude)
                Marker(position = coordinate, title = "Última ubicación")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Sin ubicación disponible")
            }
        }
    }
}

@Composable
private fun VehicleInfoPanel(uiState: VehicleDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            uiState.vehicle?.let { vehicle ->
                Text(text = "Descripción", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = vehicle.description ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            uiState.latestLocation?.let { location ->
                InfoRow("Velocidad", "${location.speed?.formatSpeed() ?: "-"}")
                InfoRow("Latitud", location.latitude.toString())
                InfoRow("Longitud", location.longitude.toString())
                InfoRow("Precisión", location.accuracy?.let { "${it} m" } ?: "-" )
                InfoRow("Actualizado", location.sampleAt.formatFriendly())
            } ?: Text(
                text = "No hay datos de ubicación recientes",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HistorySection(points: List<VehicleLocation>, onRefreshHistory: () -> Unit) {
    if (points.isEmpty()) return
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Historial reciente", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onRefreshHistory) {
                Text("Actualizar")
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(points.take(12)) { location ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(location.sampleAt.formatFriendly(), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "${location.latitude.formatCoordinate()}, ${location.longitude.formatCoordinate()}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        location.speed?.let {
                            Text("Velocidad: ${it.formatSpeed()}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

private fun Double.formatCoordinate(): String = "%.5f".format(this)

private fun Double.formatSpeed(): String = "${"%.1f".format(this)} km/h"

private fun java.time.Instant.formatFriendly(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
    return formatter.format(this.atZone(ZoneId.systemDefault()))
}
