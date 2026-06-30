package com.example.mathalarm.core.navigation

sealed class AppScreen(
    val route: String
) {
    data object Splash : AppScreen("splash")

    data object Home : AppScreen("home")

    data object AddAlarm : AppScreen("add_alarm")

    data object EditAlarm : AppScreen("edit_alarm/{alarmId}") {
        const val ARG_ALARM_ID = "alarmId"

        fun createRoute(
            alarmId: Long
        ): String {
            return "edit_alarm/$alarmId"
        }
    }

    data object Settings : AppScreen("settings")

    data object Statistics : AppScreen("statistics")

    data object AlarmRing : AppScreen("alarm_ring")

    data object MathChallenge : AppScreen("math_challenge")
}