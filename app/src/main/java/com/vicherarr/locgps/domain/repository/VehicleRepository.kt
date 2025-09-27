package com.vicherarr.locgps.domain.repository

import com.vicherarr.locgps.domain.model.Vehicle
import java.util.UUID

interface VehicleRepository {
    suspend fun getActiveVehicles(): List<Vehicle>
    suspend fun getVehicle(id: UUID): Vehicle
    suspend fun createVehicle(plate: String, description: String?): Vehicle
    suspend fun updateVehicle(id: UUID, description: String?, active: Boolean): Vehicle
}
