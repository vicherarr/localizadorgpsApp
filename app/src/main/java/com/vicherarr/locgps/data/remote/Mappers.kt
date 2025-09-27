package com.vicherarr.locgps.data.remote

import com.vicherarr.locgps.data.remote.dto.LocationDto
import com.vicherarr.locgps.data.remote.dto.LocationHistoryDto
import com.vicherarr.locgps.data.remote.dto.LoginResponseDto
import com.vicherarr.locgps.data.remote.dto.RegisterDeviceResponseDto
import com.vicherarr.locgps.data.remote.dto.VehicleDto
import com.vicherarr.locgps.domain.model.AuthSession
import com.vicherarr.locgps.domain.model.DeviceRegistration
import com.vicherarr.locgps.domain.model.LocationHistory
import com.vicherarr.locgps.domain.model.LocationSample
import com.vicherarr.locgps.domain.model.Vehicle
import com.vicherarr.locgps.domain.model.VehicleLastLocation
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.UUID

fun LoginResponseDto.toDomain(): AuthSession = AuthSession(
    token = token,
    expiresAtUtc = Instant.parse(expiraEnUtc),
    deviceId = UUID.fromString(dispositivoId),
    vehicleId = UUID.fromString(vehiculoId)
)

fun RegisterDeviceResponseDto.toDomain(): DeviceRegistration = DeviceRegistration(
    deviceId = UUID.fromString(dispositivoId),
    username = nombreUsuario
)

fun VehicleDto.toDomain(): Vehicle {
    val lastLocation = if (ultimaUbicacionUtc != null && ultimaLatitud != null && ultimaLongitud != null) {
        runCatching {
            VehicleLastLocation(
                recordedAtUtc = Instant.parse(ultimaUbicacionUtc),
                latitude = ultimaLatitud,
                longitude = ultimaLongitud
            )
        }.getOrNull()
    } else {
        null
    }
    return Vehicle(
        id = UUID.fromString(id),
        plate = placa,
        description = descripcion,
        active = activo,
        lastLocation = lastLocation
    )
}

fun LocationDto.toDomain(): LocationSample = LocationSample(
    id = id?.let { runCatching { UUID.fromString(it) }.getOrNull() },
    latitude = latitud,
    longitude = longitud,
    altitude = altitud,
    speed = velocidad,
    accuracy = precision,
    sampledAtUtc = parseInstant(fechaMuestraUtc),
    recordedAtUtc = fechaRegistroUtc?.let(::parseInstant)
)

fun LocationHistoryDto.toDomain(): LocationHistory = LocationHistory(
    vehicleId = UUID.fromString(vehiculoId),
    plate = placa,
    samples = ubicaciones.map { it.toDomain() }
)

private fun parseInstant(value: String): Instant = try {
    Instant.parse(value)
} catch (ex: DateTimeParseException) {
    // When the API sends a local datetime, parse as if it were UTC
    Instant.parse("${value}Z")
}
