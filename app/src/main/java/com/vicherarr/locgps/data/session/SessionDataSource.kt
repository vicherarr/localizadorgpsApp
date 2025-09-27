package com.vicherarr.locgps.data.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vicherarr.locgps.domain.model.AuthSession
import java.io.IOException
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private val Context.sessionDataStore by preferencesDataStore(name = "session")

class SessionDataSource(context: Context) {
    private val applicationContext = context.applicationContext
    private val store = applicationContext.sessionDataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val sessionFlow: Flow<AuthSession?> = store.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences.toSession() }

    private val state = sessionFlow.stateIn(scope, SharingStarted.Eagerly, null)

    fun observeSession(): Flow<AuthSession?> = state

    suspend fun saveSession(session: AuthSession) {
        store.edit { prefs ->
            prefs[TOKEN] = session.token
            prefs[EXPIRES_AT] = session.expiresAtUtc.toString()
            prefs[DEVICE_ID] = session.deviceId.toString()
            prefs[VEHICLE_ID] = session.vehicleId.toString()
        }
    }

    suspend fun clear() {
        store.edit { it.clear() }
    }

    fun currentToken(): String? = state.value?.takeUnless { it.isExpired }?.token

    suspend fun currentSession(): AuthSession? = state.value?.takeUnless { it.isExpired }

    private fun Preferences.toSession(): AuthSession? {
        val token = this[TOKEN] ?: return null
        val expiresAt = this[EXPIRES_AT]?.let { Instant.parse(it) } ?: return null
        val deviceId = this[DEVICE_ID]?.let { UUID.fromString(it) } ?: return null
        val vehicleId = this[VEHICLE_ID]?.let { UUID.fromString(it) } ?: return null
        val session = AuthSession(
            token = token,
            expiresAtUtc = expiresAt,
            deviceId = deviceId,
            vehicleId = vehicleId
        )
        return if (session.isExpired) null else session
    }

    private companion object {
        val TOKEN = stringPreferencesKey("token")
        val EXPIRES_AT = stringPreferencesKey("expiresAt")
        val DEVICE_ID = stringPreferencesKey("deviceId")
        val VEHICLE_ID = stringPreferencesKey("vehicleId")
    }
}
