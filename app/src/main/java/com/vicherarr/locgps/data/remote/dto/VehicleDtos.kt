package com.vicherarr.locgps.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VehicleDto(
    @SerializedName("Id")
    val id: String,
    @SerializedName("Placa")
    val plate: String,
    @SerializedName("Descripcion")
    val description: String?,
    @SerializedName("Activo")
    val isActive: Boolean,
    @SerializedName("UltimaUbicacionUtc")
    val lastLocationUtc: String?,
    @SerializedName("UltimaLatitud")
    val lastLatitude: Double?,
    @SerializedName("UltimaLongitud")
    val lastLongitude: Double?
)

data class UbicacionDto(
    @SerializedName("Id")
    val id: String,
    @SerializedName("Latitud")
    val latitude: Double,
    @SerializedName("Longitud")
    val longitude: Double,
    @SerializedName("Altitud")
    val altitude: Double?,
    @SerializedName("Velocidad")
    val speed: Double?,
    @SerializedName("Precision")
    val accuracy: Double?,
    @SerializedName("FechaMuestraUtc")
    val sampleAtUtc: String,
    @SerializedName("FechaRegistroUtc")
    val createdAtUtc: String
)

data class HistorialUbicacionesDto(
    @SerializedName("VehiculoId")
    val vehicleId: String,
    @SerializedName("Placa")
    val plate: String,
    @SerializedName("Ubicaciones")
    val points: List<UbicacionDto>
)
