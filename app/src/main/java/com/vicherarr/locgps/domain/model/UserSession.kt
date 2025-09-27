package com.vicherarr.locgps.domain.model

import java.time.Instant

data class UserSession(
    val token: String,
    val expiresAt: Instant,
    val deviceId: String,
    val vehicleId: String
) {
    val isExpired: Boolean get() = expiresAt.isBefore(Instant.now())
}
