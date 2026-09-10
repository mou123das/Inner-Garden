package com.innergarden.app.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.innergarden.app.feature.checkin.CheckInScreen
import com.innergarden.app.feature.checkin.CheckInStateHolder
import com.innergarden.app.feature.checkin.CheckInViewModel
import com.innergarden.app.feature.declutter.DeclutterScreen
import com.innergarden.app.feature.declutter.DeclutterStateHolder
import com.innergarden.app.feature.declutter.DeclutterViewModel
import com.innergarden.app.feature.home.HomeScreen
import com.innergarden.app.feature.home.HomeStateHolder
import com.innergarden.app.feature.home.HomeViewModel
import com.innergarden.app.feature.insights.InsightsScreen
import com.innergarden.app.feature.insights.InsightsStateHolder
import com.innergarden.app.feature.insights.InsightsViewModel
import com.innergarden.app.feature.reflection.ReflectionScreen
import com.innergarden.app.feature.reflection.ReflectionStateHolder
import com.innergarden.app.feature.reflection.ReflectionViewModel
import com.innergarden.app.feature.settings.SettingsScreen
import com.innergarden.app.feature.settings.SettingsViewModel
import com.innergarden.app.feature.splash.SplashScreen
import com.innergarden.app.feature.splash.SplashStateHolder
import com.innergarden.app.feature.splash.SplashViewModel
import com.innergarden.app.navigation.InnerGardenDestination
import com.innergarden.app.ui.components.InnerGardenBottomBar
import com.innergarden.app.ui.components.bottomDestinations

@Composable
fun InnerGardenApp() {
    val navController = rememberNavController()
    val declutterViewModel = viewModel<DeclutterViewModel> {
        DeclutterViewModel(
            DeclutterStateHolder(AppContainer.getRecentReflections, AppContainer.generateWeeklyDeclutter)
        )
    }
    var pendingReflection by remember { mutableStateOf("") }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomDestinations.any { it.route == currentRoute }
    val returnToGarden = {
        if (!navController.popBackStack(InnerGardenDestination.Home.route, inclusive = false)) {
            navController.navigate(InnerGardenDestination.Home.route) { launchSingleTop = true }
        }
        Unit
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                InnerGardenBottomBar(currentRoute) { route ->
                    if (route == InnerGardenDestination.Home.route) {
                        returnToGarden()
                    } else if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(InnerGardenDestination.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = InnerGardenDestination.Splash.route, modifier = Modifier.padding(padding)) {
            composable(InnerGardenDestination.Splash.route) {
                SplashScreen(
                    viewModel<SplashViewModel> { SplashViewModel(SplashStateHolder(AppContainer.ensureAuthenticated)) }
                ) {
                    navController.navigate(InnerGardenDestination.Home.route) {
                        popUpTo(InnerGardenDestination.Splash.route) { inclusive = true }
                    }
                }
            }
            composable(InnerGardenDestination.Home.route) {
                HomeScreen(
                    viewModel<HomeViewModel> {
                        HomeViewModel(HomeStateHolder(AppContainer.getRecentCheckIns, AppContainer.calculateGardenGrowth, AppContainer.calculateWellbeingScore))
                    },
                    onCheckIn = { navController.navigate(InnerGardenDestination.CheckIn.route) },
                    onReflection = {
                        pendingReflection = ""
                        navController.navigate(InnerGardenDestination.Reflection.route)
                    },
                    onSettings = { navController.navigate(InnerGardenDestination.Settings.route) }
                )
            }
            composable(InnerGardenDestination.CheckIn.route) {
                CheckInScreen(
                    viewModel<CheckInViewModel> { CheckInViewModel(CheckInStateHolder(AppContainer.saveDailyCheckIn)) },
                    navController::popBackStack
                ) { reflection ->
                    pendingReflection = reflection
                    navController.navigate(InnerGardenDestination.Reflection.route)
                }
            }
            composable(InnerGardenDestination.Reflection.route) {
                ReflectionScreen(
                    viewModel<ReflectionViewModel> {
                        ReflectionViewModel(
                            ReflectionStateHolder(
                                pendingReflection,
                                AppContainer.generateReflectionGuidance,
                                AppContainer.getPlaceholderReflectionGuidance()
                            )
                        )
                    },
                    returnToGarden
                )
            }
            composable(InnerGardenDestination.Insights.route) {
                InsightsScreen(
                    viewModel<InsightsViewModel> {
                        InsightsViewModel(InsightsStateHolder(AppContainer.getRecentCheckIns, AppContainer.calculateTrend))
                    },
                    returnToGarden
                )
            }
            composable(InnerGardenDestination.Declutter.route) {
                DeclutterScreen(
                    declutterViewModel,
                    returnToGarden
                )
            }
            composable(InnerGardenDestination.Settings.route) { SettingsScreen(viewModel<SettingsViewModel>(), navController::popBackStack) }
        }
    }
}
