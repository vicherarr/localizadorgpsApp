package com.vicherarr.locgps.data.remote.dto

data class VehicleDto(
    val id: String,
    val placa: String,
    val descripcion: String?,
    val activo: Boolean,
    val ultimaUbicacionUtc: String?,
    val ultimaLatitud: Double?,
    val ultimaLongitud: Double?
)

data class CreateVehicleRequestDto(
    val placa: String,
    val descripcion: String?
)

data class UpdateVehicleRequestDto(
    val descripcion: String?,
    val activo: Boolean
)
