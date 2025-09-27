package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.AuthSession
import com.vicherarr.locgps.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveSessionUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<AuthSession?> = repository.observeSession()
}
