package com.vicherarr.locgps.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("NombreUsuario")
    val username: String,
    @SerializedName("Contrasena")
    val password: String
)

data class LoginResponseDto(
    @SerializedName("Token")
    val token: String,
    @SerializedName("ExpiraEnUtc")
    val expiresAtUtc: String,
    @SerializedName("DispositivoId")
    val deviceId: String,
    @SerializedName("VehiculoId")
    val vehicleId: String
)
