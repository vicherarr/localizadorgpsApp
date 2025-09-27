package com.vicherarr.locgps.presentation.vehicles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.domain.model.LocationHistory
import com.vicherarr.locgps.domain.model.LocationSample
import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.usecase.GetCurrentLocationUseCase
import com.vicherarr.locgps.domain.usecase.GetLocationHistoryUseCase
import com.vicherarr.locgps.domain.usecase.GetVehicleDetailUseCase
import com.vicherarr.locgps.domain.usecase.UpdateVehicleUseCase
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getVehicleDetailUseCase: GetVehicleDetailUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getLocationHistoryUseCase: GetLocationHistoryUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase
) : ViewModel() {

    private val vehicleId: UUID = UUID.fromString(requireNotNull(savedStateHandle["vehicleId"]))

    private val _state = MutableStateFlow(VehicleDetailUiState(range = HistoryRange.default()))
    val state: StateFlow<VehicleDetailUiState> = _state

    init {
        refreshVehicle()
        loadHistory(_state.value.range)
    }

    fun refreshVehicle() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingVehicle = true, errorMessage = null) }
            runCatching {
                val vehicle = getVehicleDetailUseCase(vehicleId)
                val location = getCurrentLocationUseCase(vehicleId)
                vehicle to location
            }.onSuccess { (vehicle, location) ->
                _state.update {
                    it.copy(
                        isLoadingVehicle = false,
                        vehicle = vehicle,
                        currentLocation = location,
                        editDescription = vehicle.description.orEmpty(),
                        editActive = vehicle.active,
                        updateMessage = null,
                        updateErrorMessage = null
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoadingVehicle = false,
                        errorMessage = error.message ?: "No se pudo cargar la información"
                    )
                }
            }
        }
    }

    fun loadHistory(range: HistoryRange) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingHistory = true, errorMessage = null, range = range) }
            val now = Instant.now()
            val from = now.minus(range.duration)
            runCatching { getLocationHistoryUseCase(vehicleId, from, now) }
                .onSuccess { history ->
                    _state.update {
                        it.copy(isLoadingHistory = false, history = history)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingHistory = false,
                            errorMessage = error.message ?: "No se pudo cargar el historial"
                        )
                    }
                }
        }
    }

    fun onEditDescriptionChange(value: String) {
        _state.update { it.copy(editDescription = value) }
    }

    fun onEditActiveChange(value: Boolean) {
        _state.update { it.copy(editActive = value) }
    }

    fun updateVehicle() {
        val currentState = _state.value
        val vehicle = currentState.vehicle ?: return
        if (currentState.isUpdatingVehicle) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUpdatingVehicle = true,
                    updateErrorMessage = null,
                    updateMessage = null
                )
            }
            runCatching {
                updateVehicleUseCase(
                    UpdateVehicleUseCase.Input(
                        vehicleId = vehicle.id,
                        description = currentState.editDescription,
                        active = currentState.editActive
                    )
                )
            }.onSuccess { updated ->
                _state.update {
                    it.copy(
                        vehicle = updated,
                        editDescription = updated.description.orEmpty(),
                        editActive = updated.active,
                        isUpdatingVehicle = false,
                        updateMessage = "Vehículo actualizado correctamente",
                        updateErrorMessage = null
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isUpdatingVehicle = false,
                        updateErrorMessage = error.message ?: "No se pudo actualizar el vehículo",
                        updateMessage = null
                    )
                }
            }
        }
    }
}

data class VehicleDetailUiState(
    val vehicle: Vehicle? = null,
    val currentLocation: LocationSample? = null,
    val history: LocationHistory? = null,
    val isLoadingVehicle: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val errorMessage: String? = null,
    val range: HistoryRange,
    val editDescription: String = "",
    val editActive: Boolean = true,
    val isUpdatingVehicle: Boolean = false,
    val updateMessage: String? = null,
    val updateErrorMessage: String? = null
)

data class HistoryRange(val label: String, val duration: Duration) {
    companion object {
        fun default() = HistoryRange("2 h", Duration.ofHours(2))

        fun presets(): List<HistoryRange> = listOf(
            HistoryRange("30 min", Duration.ofMinutes(30)),
            HistoryRange("2 h", Duration.ofHours(2)),
            HistoryRange("12 h", Duration.ofHours(12)),
            HistoryRange("24 h", Duration.ofHours(24))
        )
    }
}
