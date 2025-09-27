package com.vicherarr.locgps.presentation.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.domain.usecase.CreateVehicleUseCase
import com.vicherarr.locgps.domain.usecase.RegisterDeviceUseCase
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManagementViewModel(
    private val createVehicleUseCase: CreateVehicleUseCase,
    private val registerDeviceUseCase: RegisterDeviceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ManagementUiState())
    val state: StateFlow<ManagementUiState> = _state

    fun onVehiclePlateChange(value: String) {
        _state.update { it.copy(vehicleForm = it.vehicleForm.copy(plate = value)) }
    }

    fun onVehicleDescriptionChange(value: String) {
        _state.update { it.copy(vehicleForm = it.vehicleForm.copy(description = value)) }
    }

    fun submitVehicle() {
        val plate = state.value.vehicleForm.plate.trim()
        if (plate.length !in 5..12) {
            _state.update {
                it.copy(
                    vehicleForm = it.vehicleForm.copy(
                        errorMessage = "La placa debe tener entre 5 y 12 caracteres"
                    )
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(vehicleForm = it.vehicleForm.copy(isSubmitting = true, errorMessage = null, successMessage = null))
            }
            runCatching {
                createVehicleUseCase(
                    CreateVehicleUseCase.Input(
                        plate = plate,
                        description = state.value.vehicleForm.description
                    )
                )
            }.onSuccess { vehicle ->
                _state.update {
                    it.copy(
                        vehicleForm = VehicleFormState(
                            successMessage = "Vehículo ${vehicle.plate} creado correctamente"
                        )
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        vehicleForm = it.vehicleForm.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "No se pudo registrar el vehículo"
                        )
                    )
                }
            }
        }
    }

    fun onDeviceUsernameChange(value: String) {
        _state.update { it.copy(deviceForm = it.deviceForm.copy(username = value)) }
    }

    fun onDevicePasswordChange(value: String) {
        _state.update { it.copy(deviceForm = it.deviceForm.copy(password = value)) }
    }

    fun onDeviceIdentifierChange(value: String) {
        _state.update { it.copy(deviceForm = it.deviceForm.copy(uniqueIdentifier = value)) }
    }

    fun onDeviceVehicleIdChange(value: String) {
        _state.update { it.copy(deviceForm = it.deviceForm.copy(vehicleId = value)) }
    }

    fun onDeviceDescriptionChange(value: String) {
        _state.update { it.copy(deviceForm = it.deviceForm.copy(description = value)) }
    }

    fun registerDevice() {
        val current = state.value.deviceForm
        val vehicleId = runCatching { UUID.fromString(current.vehicleId.trim()) }.getOrNull()
        val identifier = current.uniqueIdentifier.trim()
        if (current.username.trim().length < 3 || current.password.length < 6 || identifier.length < 3 || vehicleId == null) {
            _state.update {
                it.copy(
                    deviceForm = it.deviceForm.copy(
                        errorMessage = "Revisa usuario (>=3), contraseña (>=6), identificador (>=3) y el ID de vehículo"
                    )
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(deviceForm = it.deviceForm.copy(isSubmitting = true, errorMessage = null, successMessage = null))
            }
            runCatching {
                registerDeviceUseCase(
                    RegisterDeviceUseCase.Input(
                        username = current.username,
                        password = current.password,
                        uniqueIdentifier = identifier,
                        vehicleId = vehicleId,
                        description = current.description
                    )
                )
            }.onSuccess { result ->
                _state.update {
                    it.copy(
                        deviceForm = DeviceFormState(
                            successMessage = "Dispositivo ${result.username} registrado (${result.deviceId})"
                        )
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        deviceForm = it.deviceForm.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "No se pudo registrar el dispositivo"
                        )
                    )
                }
            }
        }
    }
}

data class ManagementUiState(
    val vehicleForm: VehicleFormState = VehicleFormState(),
    val deviceForm: DeviceFormState = DeviceFormState()
)

data class VehicleFormState(
    val plate: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class DeviceFormState(
    val username: String = "",
    val password: String = "",
    val uniqueIdentifier: String = "",
    val vehicleId: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
