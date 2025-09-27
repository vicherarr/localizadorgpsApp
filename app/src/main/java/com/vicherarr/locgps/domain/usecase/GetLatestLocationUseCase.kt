package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.VehicleLocation
import com.vicherarr.locgps.domain.repository.VehicleRepository

class GetLatestLocationUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: String): VehicleLocation? =
        repository.getLatestLocation(vehicleId)
}
