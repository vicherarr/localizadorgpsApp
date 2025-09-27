package com.vicherarr.locgps

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vicherarr.locgps.data.remote.api.LocalizadorGpsApi
import com.vicherarr.locgps.data.repository.AuthRepositoryImpl
import com.vicherarr.locgps.data.repository.LocationRepositoryImpl
import com.vicherarr.locgps.data.repository.VehicleRepositoryImpl
import com.vicherarr.locgps.data.session.AuthInterceptor
import com.vicherarr.locgps.data.session.SessionDataSource
import com.vicherarr.locgps.domain.repository.AuthRepository
import com.vicherarr.locgps.domain.repository.LocationRepository
import com.vicherarr.locgps.domain.repository.VehicleRepository
import com.vicherarr.locgps.domain.usecase.CreateVehicleUseCase
import com.vicherarr.locgps.domain.usecase.GetActiveVehiclesUseCase
import com.vicherarr.locgps.domain.usecase.GetCurrentLocationUseCase
import com.vicherarr.locgps.domain.usecase.GetLocationHistoryUseCase
import com.vicherarr.locgps.domain.usecase.GetVehicleDetailUseCase
import com.vicherarr.locgps.domain.usecase.LoginUseCase
import com.vicherarr.locgps.domain.usecase.LogoutUseCase
import com.vicherarr.locgps.domain.usecase.ObserveSessionUseCase
import com.vicherarr.locgps.domain.usecase.RegisterDeviceUseCase
import com.vicherarr.locgps.domain.usecase.SubmitLocationUseCase
import com.vicherarr.locgps.domain.usecase.UpdateVehicleUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AppContainer(context: Context) {
    private val sessionDataSource = SessionDataSource(context)

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { sessionDataSource.currentToken() })
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: LocalizadorGpsApi = retrofit.create(LocalizadorGpsApi::class.java)

    private val authRepository: AuthRepository = AuthRepositoryImpl(api, sessionDataSource)
    private val vehicleRepository: VehicleRepository = VehicleRepositoryImpl(api)
    private val locationRepository: LocationRepository = LocationRepositoryImpl(api)

    val observeSessionUseCase = ObserveSessionUseCase(authRepository)
    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getActiveVehiclesUseCase = GetActiveVehiclesUseCase(vehicleRepository)
    val getVehicleDetailUseCase = GetVehicleDetailUseCase(vehicleRepository)
    val getCurrentLocationUseCase = GetCurrentLocationUseCase(locationRepository)
    val getLocationHistoryUseCase = GetLocationHistoryUseCase(locationRepository)
    val submitLocationUseCase = SubmitLocationUseCase(locationRepository, authRepository)
    val createVehicleUseCase = CreateVehicleUseCase(vehicleRepository)
    val updateVehicleUseCase = UpdateVehicleUseCase(vehicleRepository)
    val registerDeviceUseCase = RegisterDeviceUseCase(authRepository)
}
