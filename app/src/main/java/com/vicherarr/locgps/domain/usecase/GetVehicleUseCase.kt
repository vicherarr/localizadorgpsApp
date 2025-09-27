package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.repository.VehicleRepository

class GetVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: String): Vehicle = repository.getVehicle(vehicleId)
}
