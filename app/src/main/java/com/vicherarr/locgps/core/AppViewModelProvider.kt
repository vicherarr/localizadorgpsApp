package com.vicherarr.locgps.core

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vicherarr.locgps.LocalizadorGpsApp
import com.vicherarr.locgps.ui.screens.login.LoginViewModel
import com.vicherarr.locgps.ui.screens.vehicles.VehiclesViewModel
import com.vicherarr.locgps.ui.screens.vehicledetail.VehicleDetailViewModel

object AppViewModelProvider {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LocalizadorGpsApp)
            LoginViewModel(application.appContainer)
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LocalizadorGpsApp)
            VehiclesViewModel(application.appContainer)
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LocalizadorGpsApp)
            val savedStateHandle = this.createSavedStateHandle()
            VehicleDetailViewModel(application.appContainer, savedStateHandle)
        }
    }
}
