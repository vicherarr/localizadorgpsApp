package com.vicherarr.locgps.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import com.vicherarr.locgps.AppContainer

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer no disponible")
}

val LocalViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("ViewModelFactory no disponible")
}
