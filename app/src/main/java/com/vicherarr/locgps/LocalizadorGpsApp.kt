package com.vicherarr.locgps

import android.app.Application
import com.vicherarr.locgps.core.AppContainer

class LocalizadorGpsApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
