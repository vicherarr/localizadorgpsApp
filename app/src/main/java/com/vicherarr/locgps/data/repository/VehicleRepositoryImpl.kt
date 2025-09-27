package com.vicherarr.locgps.data.repository

import com.vicherarr.locgps.data.remote.api.LocalizadorGpsApi
import com.vicherarr.locgps.data.remote.dto.CreateVehicleRequestDto
import com.vicherarr.locgps.data.remote.dto.UpdateVehicleRequestDto
import com.vicherarr.locgps.data.remote.toDomain
import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.repository.VehicleRepository
import java.util.UUID

class VehicleRepositoryImpl(
    private val api: LocalizadorGpsApi
) : VehicleRepository {
    override suspend fun getActiveVehicles(): List<Vehicle> =
        api.getActiveVehicles().map { it.toDomain() }

    override suspend fun getVehicle(id: UUID): Vehicle =
        api.getVehicle(id.toString()).toDomain()

    override suspend fun createVehicle(plate: String, description: String?): Vehicle {
        val response = api.createVehicle(
            CreateVehicleRequestDto(
                placa = plate,
                descripcion = description
            )
        )
        return response.toDomain()
    }

    override suspend fun updateVehicle(id: UUID, description: String?, active: Boolean): Vehicle {
        api.updateVehicle(
            vehicleId = id.toString(),
            body = UpdateVehicleRequestDto(
                descripcion = description,
                activo = active
            )
        )
        return getVehicle(id)
    }
}
