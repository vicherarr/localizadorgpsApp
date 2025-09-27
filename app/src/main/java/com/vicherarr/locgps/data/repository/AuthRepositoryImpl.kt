package com.vicherarr.locgps.data.repository

import com.vicherarr.locgps.data.remote.api.LocalizadorGpsApi
import com.vicherarr.locgps.data.remote.dto.LoginRequestDto
import com.vicherarr.locgps.data.remote.dto.RegisterDeviceRequestDto
import com.vicherarr.locgps.data.remote.toDomain
import com.vicherarr.locgps.data.session.SessionDataSource
import com.vicherarr.locgps.domain.model.AuthSession
import com.vicherarr.locgps.domain.model.DeviceRegistration
import com.vicherarr.locgps.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AuthRepositoryImpl(
    private val api: LocalizadorGpsApi,
    private val sessionDataSource: SessionDataSource
) : AuthRepository {
    override fun observeSession(): Flow<AuthSession?> = sessionDataSource.observeSession()

    override suspend fun login(username: String, password: String): AuthSession {
        val response = api.login(LoginRequestDto(nombreUsuario = username, contrasena = password))
        val session = response.toDomain()
        sessionDataSource.saveSession(session)
        return session
    }

    override suspend fun logout() {
        sessionDataSource.clear()
    }

    override suspend fun currentSession(): AuthSession? = sessionDataSource.currentSession()

    override suspend fun registerDevice(
        username: String,
        password: String,
        uniqueIdentifier: String,
        vehicleId: UUID,
        description: String?
    ): DeviceRegistration {
        val response = api.registerDevice(
            RegisterDeviceRequestDto(
                nombreUsuario = username,
                contrasena = password,
                identificadorUnico = uniqueIdentifier,
                vehiculoId = vehicleId.toString(),
                descripcionDispositivo = description
            )
        )
        return response.toDomain()
    }
}
