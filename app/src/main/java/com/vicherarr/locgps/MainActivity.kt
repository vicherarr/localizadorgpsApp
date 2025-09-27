package com.vicherarr.locgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.vicherarr.locgps.ui.navigation.LocalizadorGpsNavHost
import com.vicherarr.locgps.ui.theme.LocalizadorGpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LocalizadorGpsTheme {
                LocalizadorGpsNavHost()
            }
        }
    }
}
