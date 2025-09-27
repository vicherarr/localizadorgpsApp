package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.repository.VehicleRepository

class GetActiveVehiclesUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(): List<Vehicle> = repository.getActiveVehicles()
}
