package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.repository.VehicleRepository
import java.util.UUID

class UpdateVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(input: Input): Vehicle {
        val normalizedDescription = input.description?.trim()?.takeIf { it.isNotEmpty() }
        return repository.updateVehicle(input.vehicleId, normalizedDescription, input.active)
    }

    data class Input(
        val vehicleId: UUID,
        val description: String?,
        val active: Boolean
    )
}
