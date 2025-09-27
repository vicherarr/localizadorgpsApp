package com.vicherarr.locgps.domain.repository

import com.vicherarr.locgps.domain.model.AuthSession
import com.vicherarr.locgps.domain.model.DeviceRegistration
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AuthRepository {
    fun observeSession(): Flow<AuthSession?>
    suspend fun login(username: String, password: String): AuthSession
    suspend fun logout()
    suspend fun currentSession(): AuthSession?
    suspend fun registerDevice(
        username: String,
        password: String,
        uniqueIdentifier: String,
        vehicleId: UUID,
        description: String?
    ): DeviceRegistration
}
