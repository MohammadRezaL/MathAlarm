package com.example.mathalarm.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mathalarm.presentation.addalarm.AddAlarmRoute
import com.example.mathalarm.presentation.editalarm.EditAlarmRoute
import com.example.mathalarm.presentation.home.HomeRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route
    ) {
        composable(route = AppScreen.Home.route) {
            HomeRoute(
                paddingValues = paddingValues,
                onAddAlarmClick = {
                    navController.navigate(AppScreen.AddAlarm.route)
                },
                onAlarmClick = { alarmId ->
                    navController.navigate(
                        AppScreen.EditAlarm.createRoute(alarmId)
                    )
                }
            )
        }

        composable(route = AppScreen.AddAlarm.route) {
            AddAlarmRoute(
                paddingValues = paddingValues,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AppScreen.EditAlarm.route,
            arguments = listOf(
                navArgument(AppScreen.EditAlarm.ARG_ALARM_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getLong(
                AppScreen.EditAlarm.ARG_ALARM_ID
            ) ?: return@composable

            EditAlarmRoute(
                alarmId = alarmId,
                paddingValues = paddingValues,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}