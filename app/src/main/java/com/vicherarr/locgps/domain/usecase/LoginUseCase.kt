package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.AuthSession
import com.vicherarr.locgps.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): AuthSession {
        return repository.login(username, password)
    }
}
