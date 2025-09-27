package com.vicherarr.locgps.ui.navigation

object AppDestinations {
    const val splash = "splash"
    const val login = "login"
    const val vehicles = "vehicles"
    const val vehicleDetail = "vehicle_detail"
}

object AppNavArgs {
    const val vehicleId = "vehicleId"
}

fun vehicleDetailRoute(vehicleId: String): String = "${AppDestinations.vehicleDetail}/$vehicleId"
