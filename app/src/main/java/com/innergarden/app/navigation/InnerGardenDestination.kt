package com.innergarden.app.navigation

sealed class InnerGardenDestination(val route: String) {
    data object Splash : InnerGardenDestination("splash")
    data object Home : InnerGardenDestination("home")
    data object CheckIn : InnerGardenDestination("check_in")
    data object Reflection : InnerGardenDestination("reflection")
    data object Insights : InnerGardenDestination("insights")
    data object Declutter : InnerGardenDestination("declutter")
    data object Settings : InnerGardenDestination("settings")
}
