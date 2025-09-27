package com.vicherarr.locgps.domain.usecase

import com.vicherarr.locgps.domain.model.LocationSample
import com.vicherarr.locgps.domain.repository.LocationRepository
import java.util.UUID

class GetCurrentLocationUseCase(private val repository: LocationRepository) {
    suspend operator fun invoke(vehicleId: UUID): LocationSample? = repository.getCurrentLocation(vehicleId)
}
