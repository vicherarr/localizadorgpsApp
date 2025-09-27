package com.vicherarr.locgps.data.remote.dto

data class RegisterLocationRequestDto(
    val vehiculoId: String,
    val dispositivoId: String,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double?,
    val velocidad: Double?,
    val precision: Double?,
    val fechaMuestraUtc: String
)

data class LocationDto(
    val id: String?,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double?,
    val velocidad: Double?,
    val precision: Double?,
    val fechaMuestraUtc: String,
    val fechaRegistroUtc: String?
)

data class LocationHistoryDto(
    val vehiculoId: String,
    val placa: String,
    val ubicaciones: List<LocationDto>
)
