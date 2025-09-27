package com.vicherarr.locgps.data.repository

import com.vicherarr.locgps.data.local.SessionLocalDataSource
import com.vicherarr.locgps.data.remote.LocalizadorGpsApi
import com.vicherarr.locgps.data.remote.dto.LoginRequestDto
import com.vicherarr.locgps.domain.model.UserSession
import com.vicherarr.locgps.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class AuthRepositoryImpl(
    private val api: LocalizadorGpsApi,
    private val sessionLocalDataSource: SessionLocalDataSource
) : AuthRepository {

    override val session: Flow<UserSession?> = sessionLocalDataSource.sessionFlow

    override suspend fun login(username: String, password: String) {
        val response = api.login(LoginRequestDto(username, password))
        val session = UserSession(
            token = response.token,
            expiresAt = Instant.parse(response.expiresAtUtc),
            deviceId = response.deviceId,
            vehicleId = response.vehicleId
        )
        sessionLocalDataSource.saveSession(session)
    }

    override suspend fun logout() {
        sessionLocalDataSource.clear()
    }
}
