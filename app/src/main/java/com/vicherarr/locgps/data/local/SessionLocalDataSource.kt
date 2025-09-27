package com.vicherarr.locgps.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vicherarr.locgps.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.Instant

private val Context.sessionDataStore by preferencesDataStore(name = "session_preferences")

class SessionLocalDataSource(context: Context) {

    private val dataStore = context.sessionDataStore

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val EXPIRES_AT = stringPreferencesKey("expires_at")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val VEHICLE_ID = stringPreferencesKey("vehicle_id")
    }

    val sessionFlow: Flow<UserSession?> = dataStore.data.map { preferences ->
        val token = preferences[Keys.TOKEN]
        val expires = preferences[Keys.EXPIRES_AT]
        val deviceId = preferences[Keys.DEVICE_ID]
        val vehicleId = preferences[Keys.VEHICLE_ID]

        if (token != null && expires != null && deviceId != null && vehicleId != null) {
            UserSession(
                token = token,
                expiresAt = Instant.parse(expires),
                deviceId = deviceId,
                vehicleId = vehicleId
            )
        } else {
            null
        }
    }

    suspend fun saveSession(session: UserSession) {
        dataStore.edit { preferences ->
            preferences[Keys.TOKEN] = session.token
            preferences[Keys.EXPIRES_AT] = session.expiresAt.toString()
            preferences[Keys.DEVICE_ID] = session.deviceId
            preferences[Keys.VEHICLE_ID] = session.vehicleId
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    fun getTokenBlocking(): String? = runBlocking {
        dataStore.data.map { it[Keys.TOKEN] }.firstOrNull()
    }
}
