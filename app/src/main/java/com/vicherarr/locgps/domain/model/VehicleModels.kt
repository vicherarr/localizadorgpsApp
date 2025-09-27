package com.vicherarr.locgps.domain.model

import java.time.Instant

data class Vehicle(
    val id: String,
    val plate: String,
    val description: String?,
    val isActive: Boolean,
    val lastLocation: VehicleLocation?
)

data class VehicleLocation(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val speed: Double?,
    val accuracy: Double?,
    val sampleAt: Instant,
    val recordedAt: Instant
)

data class VehicleHistory(
    val vehicleId: String,
    val plate: String,
    val points: List<VehicleLocation>
)
