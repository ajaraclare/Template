package com.example.gym.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Routines : Screen("routines")
    object Stats: Screen("stats")
    object Profile: Screen("profile")
    object AddRoutine: Screen("add_routine")
    object RoutineDetails: Screen("routine_details")
    object SignIn: Screen("sign_in")
    object SignUp: Screen("sign_up")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

//}
//
//
//const val DASHBOARD = 0
//const val ROUTINES = 1
//const val STATS = 2
//const val PROFILE = 3
//const val ROUTINE_DETAILS = 4
//const val ADD_ROUTINES = 5