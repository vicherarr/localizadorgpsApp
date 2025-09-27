package com.vicherarr.locgps.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vicherarr.locgps.core.AppViewModelProvider
import com.vicherarr.locgps.ui.screens.login.LoginRoute
import com.vicherarr.locgps.ui.screens.login.LoginViewModel
import com.vicherarr.locgps.ui.screens.splash.SplashRoute
import com.vicherarr.locgps.ui.screens.vehicledetail.VehicleDetailRoute
import com.vicherarr.locgps.ui.screens.vehicledetail.VehicleDetailViewModel
import com.vicherarr.locgps.ui.screens.vehicles.VehiclesRoute
import com.vicherarr.locgps.ui.screens.vehicles.VehiclesViewModel

@Composable
fun LocalizadorGpsNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.splash
    ) {
        composable(AppDestinations.splash) {
            val loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
            SplashRoute(
                uiStateFlow = loginViewModel.uiState,
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.login) {
                        popUpTo(AppDestinations.splash) { inclusive = true }
                    }
                },
                onNavigateToVehicles = {
                    navController.navigate(AppDestinations.vehicles) {
                        popUpTo(AppDestinations.splash) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestinations.login) {
            val viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
            LoginRoute(
                uiStateFlow = viewModel.uiState,
                onUsernameChanged = viewModel::onUsernameChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onLogin = viewModel::login,
                onLoginSuccess = {
                    navController.navigate(AppDestinations.vehicles) {
                        popUpTo(AppDestinations.login) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestinations.vehicles) {
            val viewModel: VehiclesViewModel = viewModel(factory = AppViewModelProvider.Factory)
            VehiclesRoute(
                uiStateFlow = viewModel.uiState,
                onRefresh = viewModel::onManualRefresh,
                onLogout = viewModel::onLogout,
                onVehicleClick = { vehicleId ->
                    navController.navigate(vehicleDetailRoute(vehicleId))
                }
            )
        }
        composable(
            route = "${AppDestinations.vehicleDetail}/{${AppNavArgs.vehicleId}}",
            arguments = listOf(navArgument(AppNavArgs.vehicleId) { type = NavType.StringType })
        ) {
            val viewModel: VehicleDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
            VehicleDetailRoute(
                uiStateFlow = viewModel.uiState,
                onRefresh = viewModel::refreshAll,
                onRefreshHistory = viewModel::refreshHistory,
                onRefreshLocation = viewModel::refreshLocationSilently,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
