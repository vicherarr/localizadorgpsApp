package com.vicherarr.locgps.data.repository

import com.vicherarr.locgps.data.remote.api.LocalizadorGpsApi
import com.vicherarr.locgps.data.remote.dto.RegisterLocationRequestDto
import com.vicherarr.locgps.data.remote.toDomain
import com.vicherarr.locgps.domain.model.LocationHistory
import com.vicherarr.locgps.domain.model.LocationSample
import com.vicherarr.locgps.domain.model.SubmitLocationParams
import com.vicherarr.locgps.domain.repository.LocationRepository
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID
import retrofit2.HttpException

class LocationRepositoryImpl(
    private val api: LocalizadorGpsApi
) : LocationRepository {

    override suspend fun getCurrentLocation(vehicleId: UUID): LocationSample? = try {
        api.getCurrentLocation(vehicleId.toString()).toDomain()
    } catch (ex: HttpException) {
        if (ex.code() == 404) {
            null
        } else {
            throw ex
        }
    }

    override suspend fun getLocationHistory(vehicleId: UUID, fromUtc: Instant, toUtc: Instant): LocationHistory {
        val response = api.getLocationHistory(
            vehicleId = vehicleId.toString(),
            fromUtc = DateTimeFormatter.ISO_INSTANT.format(fromUtc),
            toUtc = DateTimeFormatter.ISO_INSTANT.format(toUtc)
        )
        return response.toDomain()
    }

    override suspend fun submitLocation(params: SubmitLocationParams) {
        api.submitLocation(
            RegisterLocationRequestDto(
                vehiculoId = params.vehicleId.toString(),
                dispositivoId = params.deviceId.toString(),
                latitud = params.latitude,
                longitud = params.longitude,
                altitud = params.altitude,
                velocidad = params.speed,
                precision = params.accuracy,
                fechaMuestraUtc = DateTimeFormatter.ISO_INSTANT.format(params.sampledAtUtc)
            )
        )
    }
}
