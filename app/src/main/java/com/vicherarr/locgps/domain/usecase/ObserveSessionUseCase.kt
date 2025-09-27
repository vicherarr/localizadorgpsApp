package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.UserSession
import com.vicherarr.locgps.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveSessionUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<UserSession?> = repository.session
}
