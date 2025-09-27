package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.DeviceRegistration
import com.vicherarr.locgps.domain.repository.AuthRepository
import java.util.UUID

class RegisterDeviceUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(input: Input): DeviceRegistration {
        return repository.registerDevice(
            username = input.username.trim(),
            password = input.password,
            uniqueIdentifier = input.uniqueIdentifier.trim(),
            vehicleId = input.vehicleId,
            description = input.description?.trim()?.takeIf { it.isNotEmpty() }
        )
    }

    data class Input(
        val username: String,
        val password: String,
        val uniqueIdentifier: String,
        val vehicleId: UUID,
        val description: String?
    )
}
