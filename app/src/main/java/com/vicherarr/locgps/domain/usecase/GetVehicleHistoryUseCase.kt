package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.VehicleHistory
import com.vicherarr.locgps.domain.repository.VehicleRepository

class GetVehicleHistoryUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(
        vehicleId: String,
        fromUtc: String? = null,
        toUtc: String? = null
    ): VehicleHistory = repository.getLocationHistory(vehicleId, fromUtc, toUtc)
}
