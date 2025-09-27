package com.vicherarr.locgps.data.remote.dto

data class LoginRequestDto(
    val nombreUsuario: String,
    val contrasena: String
)

data class LoginResponseDto(
    val token: String,
    val expiraEnUtc: String,
    val dispositivoId: String,
    val vehiculoId: String
)

data class RegisterDeviceRequestDto(
    val nombreUsuario: String,
    val contrasena: String,
    val identificadorUnico: String,
    val vehiculoId: String,
    val descripcionDispositivo: String?
)

data class RegisterDeviceResponseDto(
    val dispositivoId: String,
    val nombreUsuario: String
)
