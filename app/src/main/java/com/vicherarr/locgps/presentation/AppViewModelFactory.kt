package com.vicherarr.locgps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.vicherarr.locgps.AppContainer
import com.vicherarr.locgps.presentation.auth.AuthViewModel
import com.vicherarr.locgps.presentation.management.ManagementViewModel
import com.vicherarr.locgps.presentation.session.SessionViewModel
import com.vicherarr.locgps.presentation.vehicles.VehicleDetailViewModel
import com.vicherarr.locgps.presentation.vehicles.VehiclesViewModel
import com.vicherarr.locgps.presentation.vehicles.submit.SubmitLocationViewModel

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = runCatching { extras.createSavedStateHandle() }.getOrNull()
        val viewModel: ViewModel = when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(container.loginUseCase)
            VehiclesViewModel::class.java -> VehiclesViewModel(container.getActiveVehiclesUseCase)
            VehicleDetailViewModel::class.java -> VehicleDetailViewModel(
                savedStateHandle = requireNotNull(savedStateHandle) {
                    "VehicleDetailViewModel requiere SavedStateHandle"
                },
                getVehicleDetailUseCase = container.getVehicleDetailUseCase,
                getCurrentLocationUseCase = container.getCurrentLocationUseCase,
                getLocationHistoryUseCase = container.getLocationHistoryUseCase,
                updateVehicleUseCase = container.updateVehicleUseCase
            )
            SessionViewModel::class.java -> SessionViewModel(
                observeSessionUseCase = container.observeSessionUseCase,
                logoutUseCase = container.logoutUseCase
            )
            SubmitLocationViewModel::class.java -> SubmitLocationViewModel(container.submitLocationUseCase)
            ManagementViewModel::class.java -> ManagementViewModel(
                createVehicleUseCase = container.createVehicleUseCase,
                registerDeviceUseCase = container.registerDeviceUseCase
            )
            else -> throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
        }
        return viewModel as T
    }
}
