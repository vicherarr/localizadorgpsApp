package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.repository.VehicleRepository

class CreateVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(input: Input): Vehicle {
        val normalizedDescription = input.description?.trim()?.takeIf { it.isNotEmpty() }
        return repository.createVehicle(input.plate.trim(), normalizedDescription)
    }

    data class Input(
        val plate: String,
        val description: String?
    )
}
