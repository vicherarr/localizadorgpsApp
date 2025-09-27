package com.vicherarr.locgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vicherarr.locgps.presentation.AppViewModelFactory
import com.vicherarr.locgps.presentation.LocalAppContainer
import com.vicherarr.locgps.presentation.LocalViewModelFactory
import com.vicherarr.locgps.presentation.LocalizadorGpsApp
import com.vicherarr.locgps.presentation.session.SessionViewModel
import com.vicherarr.locgps.ui.theme.LocalizadorGpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = AppContainer(applicationContext)
        val viewModelFactory = AppViewModelFactory(container)
        setContent {
            LocalizadorGpsTheme {
                CompositionLocalProvider(
                    LocalAppContainer provides container,
                    LocalViewModelFactory provides viewModelFactory
                ) {
                    val sessionViewModel: SessionViewModel = viewModel(factory = viewModelFactory)
                    LocalizadorGpsApp(sessionViewModel = sessionViewModel)
                }
            }
        }
    }
}