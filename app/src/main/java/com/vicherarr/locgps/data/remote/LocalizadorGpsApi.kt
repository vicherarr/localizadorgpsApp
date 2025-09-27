package com.vicherarr.locgps.data.remote

import com.vicherarr.locgps.data.remote.dto.HistorialUbicacionesDto
import com.vicherarr.locgps.data.remote.dto.LoginRequestDto
import com.vicherarr.locgps.data.remote.dto.LoginResponseDto
import com.vicherarr.locgps.data.remote.dto.UbicacionDto
import com.vicherarr.locgps.data.remote.dto.VehicleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LocalizadorGpsApi {

    @POST("api/autenticacion/inicio-sesion")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @GET("api/vehiculos/activos")
    suspend fun getActiveVehicles(): List<VehicleDto>

    @GET("api/vehiculos/{id}")
    suspend fun getVehicle(@Path("id") vehicleId: String): VehicleDto

    @GET("api/ubicaciones/vehiculos/{id}/actual")
    suspend fun getLatestLocation(@Path("id") vehicleId: String): UbicacionDto

    @GET("api/ubicaciones/vehiculos/{id}/historial")
    suspend fun getLocationHistory(
        @Path("id") vehicleId: String,
        @Query("desdeUtc") fromUtc: String?,
        @Query("hastaUtc") toUtc: String?
    ): HistorialUbicacionesDto
}
