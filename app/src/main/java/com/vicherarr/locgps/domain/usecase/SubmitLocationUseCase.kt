package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.SubmitLocationParams
import com.vicherarr.locgps.domain.repository.AuthRepository
import com.vicherarr.locgps.domain.repository.LocationRepository
import java.time.Instant
import java.util.UUID

class SubmitLocationUseCase(
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(input: Input) {
        val session = authRepository.currentSession()
            ?: throw IllegalStateException("Debe iniciar sesión para registrar ubicaciones")
        val params = SubmitLocationParams(
            vehicleId = input.vehicleId ?: session.vehicleId,
            deviceId = input.deviceId ?: session.deviceId,
            latitude = input.latitude,
            longitude = input.longitude,
            altitude = input.altitude,
            speed = input.speed,
            accuracy = input.accuracy,
            sampledAtUtc = input.sampledAtUtc
        )
        locationRepository.submitLocation(params)
    }

    data class Input(
        val vehicleId: UUID? = null,
        val deviceId: UUID? = null,
        val latitude: Double,
        val longitude: Double,
        val altitude: Double? = null,
        val speed: Double? = null,
        val accuracy: Double? = null,
        val sampledAtUtc: Instant
    )
}
