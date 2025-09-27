package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
