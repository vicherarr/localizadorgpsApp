package com.vicherarr.locgps.domain.model

import java.time.Instant
import java.util.UUID

data class AuthSession(
    val token: String,
    val expiresAtUtc: Instant,
    val deviceId: UUID,
    val vehicleId: UUID
) {
    val isExpired: Boolean
        get() = Instant.now().isAfter(expiresAtUtc)
}
