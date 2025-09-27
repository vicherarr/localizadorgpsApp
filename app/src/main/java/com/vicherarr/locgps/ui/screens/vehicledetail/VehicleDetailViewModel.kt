package com.vicherarr.locgps.ui.screens.vehicledetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.core.AppContainer
import com.vicherarr.locgps.domain.usecase.GetLatestLocationUseCase
import com.vicherarr.locgps.domain.usecase.GetVehicleHistoryUseCase
import com.vicherarr.locgps.domain.usecase.GetVehicleUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class VehicleDetailViewModel(
    appContainer: AppContainer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: String = requireNotNull(savedStateHandle["vehicleId"]) {
        "Se requiere el identificador del vehículo"
    }

    private val getVehicleUseCase = GetVehicleUseCase(appContainer.vehicleRepository)
    private val getLatestLocationUseCase = GetLatestLocationUseCase(appContainer.vehicleRepository)
    private val getVehicleHistoryUseCase = GetVehicleHistoryUseCase(appContainer.vehicleRepository)

    private val _uiState = MutableStateFlow(VehicleDetailUiState())
    val uiState: StateFlow<VehicleDetailUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _uiState.value = VehicleDetailUiState(isLoading = true)
            try {
                val vehicle = getVehicleUseCase(vehicleId)
                val latestLocation = getLatestLocationUseCase(vehicleId) ?: vehicle.lastLocation
                val history = runCatching { getVehicleHistoryUseCase(vehicleId) }.getOrNull()
                _uiState.value = VehicleDetailUiState(
                    isLoading = false,
                    vehicle = vehicle,
                    latestLocation = latestLocation,
                    history = history
                )
            } catch (exception: Exception) {
                _uiState.value = VehicleDetailUiState(
                    isLoading = false,
                    errorMessage = exception.message ?: "No fue posible cargar el vehículo"
                )
            }
        }
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            while (true) {
                delay(15.seconds)
                refreshLocationSilently()
            }
        }
    }

    fun refreshLocationSilently() {
        viewModelScope.launch {
            try {
                val latest = getLatestLocationUseCase(vehicleId)
                if (latest != null) {
                    _uiState.value = _uiState.value.copy(latestLocation = latest, errorMessage = null)
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = exception.message)
            }
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            try {
                val history = getVehicleHistoryUseCase(vehicleId)
                _uiState.value = _uiState.value.copy(history = history, errorMessage = null)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = exception.message)
            }
        }
    }

    override fun onCleared() {
        locationJob?.cancel()
        super.onCleared()
    }
}
