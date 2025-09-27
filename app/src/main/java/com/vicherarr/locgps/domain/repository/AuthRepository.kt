package com.vicherarr.locgps.domain.repository

import com.vicherarr.locgps.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<UserSession?>
    suspend fun login(username: String, password: String)
    suspend fun logout()
}
