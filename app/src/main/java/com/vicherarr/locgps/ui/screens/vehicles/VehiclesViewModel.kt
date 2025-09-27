package com.vicherarr.locgps.ui.screens.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.core.AppContainer
import com.vicherarr.locgps.domain.usecase.GetActiveVehiclesUseCase
import com.vicherarr.locgps.domain.usecase.LogoutUseCase
import com.vicherarr.locgps.domain.usecase.ObserveSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class VehiclesViewModel(appContainer: AppContainer) : ViewModel() {

    private val getActiveVehicles = GetActiveVehiclesUseCase(appContainer.vehicleRepository)
    private val logoutUseCase = LogoutUseCase(appContainer.authRepository)
    private val observeSessionUseCase = ObserveSessionUseCase(appContainer.authRepository)

    private val _uiState = MutableStateFlow(VehiclesUiState(isLoading = true))
    val uiState: StateFlow<VehiclesUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        observeSession()
        refreshVehicles()
    }

    private fun observeSession() {
        viewModelScope.launch {
            observeSessionUseCase().collectLatest { session ->
                if (session == null || session.isExpired) {
                    _uiState.value = VehiclesUiState()
                }
            }
        }
    }

    fun refreshVehicles() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                loadVehiclesOnce()
                delay(30.seconds)
            }
        }
    }

    private suspend fun loadVehiclesOnce() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        try {
            val vehicles = getActiveVehicles()
            _uiState.value = VehiclesUiState(isLoading = false, vehicles = vehicles)
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = exception.message ?: "Error al cargar la flota"
            )
        }
    }

    fun onManualRefresh() {
        viewModelScope.launch {
            loadVehiclesOnce()
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    override fun onCleared() {
        refreshJob?.cancel()
        super.onCleared()
    }
}
