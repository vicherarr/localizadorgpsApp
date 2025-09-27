package com.vicherarr.locgps.ui.screens.vehicles

import com.vicherarr.locgps.domain.model.Vehicle

data class VehiclesUiState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val errorMessage: String? = null
)
