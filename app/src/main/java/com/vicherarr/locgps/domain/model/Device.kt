package com.vicherarr.locgps.domain.model

import java.util.UUID

data class DeviceRegistration(
    val deviceId: UUID,
    val username: String
)
