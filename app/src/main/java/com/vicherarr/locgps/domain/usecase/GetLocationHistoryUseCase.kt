package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.LocationHistory
import com.vicherarr.locgps.domain.repository.LocationRepository
import java.time.Instant
import java.util.UUID

class GetLocationHistoryUseCase(private val repository: LocationRepository) {
    suspend operator fun invoke(vehicleId: UUID, fromUtc: Instant, toUtc: Instant): LocationHistory {
        return repository.getLocationHistory(vehicleId, fromUtc, toUtc)
    }
}
