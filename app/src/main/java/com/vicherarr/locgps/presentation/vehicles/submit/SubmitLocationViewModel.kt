package com.vicherarr.locgps.presentation.vehicles.submit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vicherarr.locgps.domain.usecase.SubmitLocationUseCase
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubmitLocationViewModel(
    private val submitLocationUseCase: SubmitLocationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SubmitLocationUiState())
    val state: StateFlow<SubmitLocationUiState> = _state

    fun onLatitudeChange(value: String) {
        _state.update { it.copy(latitude = value) }
    }

    fun onLongitudeChange(value: String) {
        _state.update { it.copy(longitude = value) }
    }

    fun onAltitudeChange(value: String) {
        _state.update { it.copy(altitude = value) }
    }

    fun onSpeedChange(value: String) {
        _state.update { it.copy(speed = value) }
    }

    fun onAccuracyChange(value: String) {
        _state.update { it.copy(accuracy = value) }
    }

    fun submit(onSuccess: () -> Unit) {
        val latitude = state.value.latitude.toDoubleOrNull()
        val longitude = state.value.longitude.toDoubleOrNull()
        if (latitude == null || longitude == null) {
            _state.update { it.copy(errorMessage = "Latitud y longitud son obligatorias") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            val input = SubmitLocationUseCase.Input(
                latitude = latitude,
                longitude = longitude,
                altitude = state.value.altitude.toDoubleOrNull(),
                speed = state.value.speed.toDoubleOrNull(),
                accuracy = state.value.accuracy.toDoubleOrNull(),
                sampledAtUtc = Instant.now()
            )
            runCatching { submitLocationUseCase(input) }
                .onSuccess {
                    _state.update {
                        SubmitLocationUiState(successMessage = "Ubicación enviada correctamente")
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "No se pudo registrar la ubicación"
                        )
                    }
                }
        }
    }
}

data class SubmitLocationUiState(
    val latitude: String = "",
    val longitude: String = "",
    val altitude: String = "",
    val speed: String = "",
    val accuracy: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
