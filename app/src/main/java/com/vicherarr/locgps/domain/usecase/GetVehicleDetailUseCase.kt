package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.repository.VehicleRepository
import java.util.UUID

class GetVehicleDetailUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicleId: UUID): Vehicle = repository.getVehicle(vehicleId)
}
