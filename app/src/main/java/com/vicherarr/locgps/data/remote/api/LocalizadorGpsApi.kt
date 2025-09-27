package com.vicherarr.locgps.data.remote.api

import com.vicherarr.locgps.data.remote.dto.CreateVehicleRequestDto
import com.vicherarr.locgps.data.remote.dto.LocationDto
import com.vicherarr.locgps.data.remote.dto.LocationHistoryDto
import com.vicherarr.locgps.data.remote.dto.LoginRequestDto
import com.vicherarr.locgps.data.remote.dto.LoginResponseDto
import com.vicherarr.locgps.data.remote.dto.RegisterDeviceRequestDto
import com.vicherarr.locgps.data.remote.dto.RegisterDeviceResponseDto
import com.vicherarr.locgps.data.remote.dto.RegisterLocationRequestDto
import com.vicherarr.locgps.data.remote.dto.UpdateVehicleRequestDto
import com.vicherarr.locgps.data.remote.dto.VehicleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LocalizadorGpsApi {
    @POST("api/autenticacion/inicio-sesion")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("api/autenticacion/registro")
    suspend fun registerDevice(@Body body: RegisterDeviceRequestDto): RegisterDeviceResponseDto

    @GET("api/vehiculos/activos")
    suspend fun getActiveVehicles(): List<VehicleDto>

    @GET("api/vehiculos/{id}")
    suspend fun getVehicle(@Path("id") vehicleId: String): VehicleDto

    @POST("api/vehiculos")
    suspend fun createVehicle(@Body body: CreateVehicleRequestDto): VehicleDto

    @PUT("api/vehiculos/{id}")
    suspend fun updateVehicle(
        @Path("id") vehicleId: String,
        @Body body: UpdateVehicleRequestDto
    )

    @GET("api/ubicaciones/vehiculos/{id}/actual")
    suspend fun getCurrentLocation(@Path("id") vehicleId: String): LocationDto

    @GET("api/ubicaciones/vehiculos/{id}/historial")
    suspend fun getLocationHistory(
        @Path("id") vehicleId: String,
        @Query("desdeUtc") fromUtc: String,
        @Query("hastaUtc") toUtc: String
    ): LocationHistoryDto

    @POST("api/ubicaciones")
    suspend fun submitLocation(@Body body: RegisterLocationRequestDto)
}
