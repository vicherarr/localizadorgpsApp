package com.vicherarr.locgps.domain.model

import java.time.Instant
import java.util.UUID

data class Vehicle(
    val id: UUID,
    val plate: String,
    val description: String?,
    val active: Boolean,
    val lastLocation: VehicleLastLocation?
)

data class VehicleLastLocation(
    val recordedAtUtc: Instant,
    val latitude: Double,
    val longitude: Double
)
