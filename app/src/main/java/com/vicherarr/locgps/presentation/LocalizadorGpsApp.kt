package com.vicherarr.locgps.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.vicherarr.locgps.presentation.auth.LoginScreen
import com.vicherarr.locgps.presentation.management.ManagementScreen
import com.vicherarr.locgps.presentation.session.SessionViewModel
import com.vicherarr.locgps.presentation.session.collectSessionState
import com.vicherarr.locgps.presentation.vehicles.VehicleDetailScreen
import com.vicherarr.locgps.presentation.vehicles.VehiclesScreen
import com.vicherarr.locgps.presentation.vehicles.submit.SubmitLocationScreen

sealed class AppScreen(val route: String, val label: String) {
    object Login : AppScreen("login", "Ingreso")
    object Vehicles : AppScreen("vehicles", "Flota")
    object SubmitLocation : AppScreen("submit_location", "Reportar")
    object Management : AppScreen("management", "Admin")
    object VehicleDetail : AppScreen("vehicle/{vehicleId}", "Detalle") {
        fun createRoute(vehicleId: String) = "vehicle/$vehicleId"
    }
}

@Composable
fun LocalizadorGpsApp(sessionViewModel: SessionViewModel) {
    val sessionState = sessionViewModel.collectSessionState()
    val navController = rememberNavController()

    LaunchedEffect(sessionState.isAuthenticated, sessionState.isLoading) {
        if (sessionState.isLoading) return@LaunchedEffect
        val currentRoute = navController.currentDestination?.route
        if (sessionState.isAuthenticated && currentRoute != AppScreen.Vehicles.route) {
            navController.navigate(AppScreen.Vehicles.route, navOptions {
                popUpTo(AppScreen.Login.route) { inclusive = true }
                launchSingleTop = true
            })
        } else if (!sessionState.isAuthenticated && currentRoute != AppScreen.Login.route) {
            navController.navigate(AppScreen.Login.route, navOptions {
                popUpTo(AppScreen.Vehicles.route) { inclusive = true }
                launchSingleTop = true
            })
        }
    }

    AppScaffold(
        navController = navController,
        sessionViewModel = sessionViewModel,
        sessionState = sessionState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    sessionState: com.vicherarr.locgps.presentation.session.SessionUiState
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val shouldShowBottomBar = currentDestination?.route in listOf(
        AppScreen.Vehicles.route,
        AppScreen.SubmitLocation.route,
        AppScreen.Management.route
    )

    Scaffold(
        topBar = {
            when (currentDestination?.route) {
                AppScreen.Vehicles.route -> {
                    TopAppBar(
                        title = { Text(text = "Vehículos activos") },
                        actions = {
                            IconButton(onClick = { sessionViewModel.logout() }) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                            }
                        }
                    )
                }
                AppScreen.SubmitLocation.route -> {
                    TopAppBar(
                        title = { Text(text = "Registrar ubicación") }
                    )
                }
                AppScreen.Management.route -> {
                    TopAppBar(title = { Text(text = "Administración") })
                }
                AppScreen.Login.route -> {
                    TopAppBar(title = { Text("Localizador GPS") })
                }
                AppScreen.VehicleDetail.route -> {
                    TopAppBar(
                        title = { Text("Detalle de vehículo") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    val destinations = listOf(
                        AppScreen.Vehicles,
                        AppScreen.SubmitLocation,
                        AppScreen.Management
                    )
                    destinations.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                when (screen) {
                                    AppScreen.Vehicles -> Icon(Icons.Filled.Map, contentDescription = null)
                                    AppScreen.SubmitLocation -> Icon(Icons.Filled.AddLocationAlt, contentDescription = null)
                                    AppScreen.Management -> Icon(Icons.Filled.Settings, contentDescription = null)
                                    else -> {}
                                }
                            },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = AppScreen.Login.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(AppScreen.Login.route) {
                    LoginScreen(onLoggedIn = {
                        navController.navigate(AppScreen.Vehicles.route) {
                            popUpTo(AppScreen.Login.route) { inclusive = true }
                        }
                    })
                }
                composable(AppScreen.Vehicles.route) {
                    VehiclesScreen(onVehicleSelected = { vehicleId ->
                        navController.navigate(AppScreen.VehicleDetail.createRoute(vehicleId))
                    })
                }
                composable(AppScreen.SubmitLocation.route) {
                    SubmitLocationScreen()
                }
                composable(AppScreen.Management.route) {
                    ManagementScreen()
                }
                composable(
                    route = AppScreen.VehicleDetail.route,
                    arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
                ) {
                    VehicleDetailScreen()
                }
            }
            if (sessionState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
