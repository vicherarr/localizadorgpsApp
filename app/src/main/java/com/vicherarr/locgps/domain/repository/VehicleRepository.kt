package com.vicherarr.locgps.domain.repository

import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.model.VehicleHistory
import com.vicherarr.locgps.domain.model.VehicleLocation

interface VehicleRepository {
    suspend fun getActiveVehicles(): List<Vehicle>
    suspend fun getVehicle(vehicleId: String): Vehicle
    suspend fun getLatestLocation(vehicleId: String): VehicleLocation?
    suspend fun getLocationHistory(
        vehicleId: String,
        fromUtc: String? = null,
        toUtc: String? = null
    ): VehicleHistory
}
