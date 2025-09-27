package com.vicherarr.locgps.data.repository

import com.vicherarr.locgps.data.remote.LocalizadorGpsApi
import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.model.VehicleHistory
import com.vicherarr.locgps.domain.model.VehicleLocation
import com.vicherarr.locgps.domain.repository.VehicleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.Instant

class VehicleRepositoryImpl(
    private val api: LocalizadorGpsApi,
    private val ioDispatcher: CoroutineDispatcher
) : VehicleRepository {

    override suspend fun getActiveVehicles(): List<Vehicle> = withContext(ioDispatcher) {
        api.getActiveVehicles().map { dto ->
            Vehicle(
                id = dto.id,
                plate = dto.plate,
                description = dto.description,
                isActive = dto.isActive,
                lastLocation = if (dto.lastLatitude != null && dto.lastLongitude != null && dto.lastLocationUtc != null) {
                    VehicleLocation(
                        latitude = dto.lastLatitude,
                        longitude = dto.lastLongitude,
                        altitude = null,
                        speed = null,
                        accuracy = null,
                        sampleAt = Instant.parse(dto.lastLocationUtc),
                        recordedAt = Instant.parse(dto.lastLocationUtc)
                    )
                } else {
                    null
                }
            )
        }
    }

    override suspend fun getVehicle(vehicleId: String): Vehicle = withContext(ioDispatcher) {
        api.getVehicle(vehicleId).let { dto ->
            Vehicle(
                id = dto.id,
                plate = dto.plate,
                description = dto.description,
                isActive = dto.isActive,
                lastLocation = if (dto.lastLatitude != null && dto.lastLongitude != null && dto.lastLocationUtc != null) {
                    VehicleLocation(
                        latitude = dto.lastLatitude,
                        longitude = dto.lastLongitude,
                        altitude = null,
                        speed = null,
                        accuracy = null,
                        sampleAt = Instant.parse(dto.lastLocationUtc),
                        recordedAt = Instant.parse(dto.lastLocationUtc)
                    )
                } else {
                    null
                }
            )
        }
    }

    override suspend fun getLatestLocation(vehicleId: String): VehicleLocation? = withContext(ioDispatcher) {
        try {
            api.getLatestLocation(vehicleId).toDomain()
        } catch (ex: HttpException) {
            if (ex.code() == 404) null else throw ex
        }
    }

    override suspend fun getLocationHistory(
        vehicleId: String,
        fromUtc: String?,
        toUtc: String?
    ): VehicleHistory = withContext(ioDispatcher) {
        api.getLocationHistory(vehicleId, fromUtc, toUtc).let { dto ->
            VehicleHistory(
                vehicleId = dto.vehicleId,
                plate = dto.plate,
                points = dto.points.map { it.toDomain() }
            )
        }
    }

    private fun com.vicherarr.locgps.data.remote.dto.UbicacionDto.toDomain(): VehicleLocation =
        VehicleLocation(
            latitude = latitude,
            longitude = longitude,
            altitude = altitude,
            speed = speed,
            accuracy = accuracy,
            sampleAt = Instant.parse(sampleAtUtc),
            recordedAt = Instant.parse(createdAtUtc)
        )
}
