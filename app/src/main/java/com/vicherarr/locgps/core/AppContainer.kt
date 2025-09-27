package com.vicherarr.locgps.core

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.vicherarr.locgps.data.local.SessionLocalDataSource
import com.vicherarr.locgps.data.remote.AuthorizationInterceptor
import com.vicherarr.locgps.data.remote.LocalizadorGpsApi
import com.vicherarr.locgps.data.repository.AuthRepositoryImpl
import com.vicherarr.locgps.data.repository.VehicleRepositoryImpl
import com.vicherarr.locgps.domain.repository.AuthRepository
import com.vicherarr.locgps.domain.repository.VehicleRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val sessionDataSource = SessionLocalDataSource(context)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor { sessionDataSource.getTokenBlocking() })
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfigProvider.baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val api: LocalizadorGpsApi = retrofit.create(LocalizadorGpsApi::class.java)

    val authRepository: AuthRepository = AuthRepositoryImpl(api, sessionDataSource)

    val vehicleRepository: VehicleRepository = VehicleRepositoryImpl(
        api = api,
        ioDispatcher = Dispatchers.IO
    )

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
}

object BuildConfigProvider {
    val baseUrl: String
        get() = com.vicherarr.locgps.BuildConfig.API_BASE_URL
}
