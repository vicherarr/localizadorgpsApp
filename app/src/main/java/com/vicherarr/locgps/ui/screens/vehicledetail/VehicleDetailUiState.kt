package com.vicherarr.locgps.ui.screens.vehicledetail

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.model.VehicleHistory
import com.vicherarr.locgps.domain.model.VehicleLocation

data class VehicleDetailUiState(
    val isLoading: Boolean = true,
    val vehicle: Vehicle? = null,
    val latestLocation: VehicleLocation? = null,
    val history: VehicleHistory? = null,
    val errorMessage: String? = null
)
