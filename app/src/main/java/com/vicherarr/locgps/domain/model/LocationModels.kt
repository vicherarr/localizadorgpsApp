package com.vicherarr.locgps.domain.model

import java.time.Instant
import java.util.UUID

data class LocationSample(
    val id: UUID?,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val speed: Double?,
    val accuracy: Double?,
    val sampledAtUtc: Instant,
    val recordedAtUtc: Instant?
)

data class LocationHistory(
    val vehicleId: UUID,
    val plate: String,
    val samples: List<LocationSample>
)

data class SubmitLocationParams(
    val vehicleId: UUID,
    val deviceId: UUID,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val speed: Double?,
    val accuracy: Double?,
    val sampledAtUtc: Instant
)
