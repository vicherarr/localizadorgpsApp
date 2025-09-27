package com.vicherarr.locgps.domain.repository

import com.vicherarr.locgps.domain.model.LocationHistory
import com.vicherarr.locgps.domain.model.LocationSample
import com.vicherarr.locgps.domain.model.SubmitLocationParams
import java.time.Instant
import java.util.UUID

interface LocationRepository {
    suspend fun getCurrentLocation(vehicleId: UUID): LocationSample?
    suspend fun getLocationHistory(vehicleId: UUID, fromUtc: Instant, toUtc: Instant): LocationHistory
    suspend fun submitLocation(params: SubmitLocationParams)
}
