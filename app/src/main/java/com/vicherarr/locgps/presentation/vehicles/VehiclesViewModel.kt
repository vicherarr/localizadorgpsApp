package com.vicherarr.locgps.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.usecase.GetActiveVehiclesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehiclesViewModel(
    private val getActiveVehiclesUseCase: GetActiveVehiclesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VehiclesUiState())
    val state: StateFlow<VehiclesUiState> = _state

    private var autoRefreshJob: Job? = null

    init {
        refresh(force = true)
        scheduleAutoRefresh()
    }

    fun refresh(force: Boolean = false) {
        if (_state.value.isLoading && !force) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching { getActiveVehiclesUseCase() }
                .onSuccess { vehicles ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            vehicles = vehicles,
                            errorMessage = null,
                            infoMessage = if (vehicles.isEmpty()) "No hay vehículos activos" else null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo obtener la flota",
                            infoMessage = null
                        )
                    }
                }
        }
    }

    private fun scheduleAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(60_000)
                refresh(force = true)
            }
        }
    }

    override fun onCleared() {
        autoRefreshJob?.cancel()
        super.onCleared()
    }
}

data class VehiclesUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
