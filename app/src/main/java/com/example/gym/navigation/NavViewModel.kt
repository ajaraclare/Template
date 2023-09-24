package com.example.gym.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.gym.ui.theme.Green700

class NavViewModel : ViewModel() {
    private var currentScreen: Screen by mutableStateOf(Screen.Dashboard)
    var appBarText by mutableStateOf("Dashboard")
        private set
    var backButtonEnabled by mutableStateOf(false)
        private set
    var backButtonColor by mutableStateOf(Color.Transparent)
        private set
    private var lastScreen: Screen by mutableStateOf(Screen.Dashboard)
    var signedIn by mutableStateOf(false)
        private set

    fun switchScreen(screen: Screen) {
        lastScreen = currentScreen
        currentScreen = screen
        appBarText = getScreenBarText(screen)
        if (screen == Screen.AddRoutine || screen == Screen.RoutineDetails) {
            backButtonEnabled = true
            backButtonColor = Green700
        }
    }

    fun updateTopBarText(text: String) {
        appBarText = text
    }

    fun switchBackToScreen(): Screen {
        currentScreen = lastScreen
        backButtonEnabled = false
        backButtonColor = Color.Transparent
        appBarText = getScreenBarText(lastScreen)
        return lastScreen
    }

    private fun getScreenBarText(screen: Screen): String {
        when (screen) {
            Screen.Dashboard -> {
                return "Dashboard"
            }
            Screen.Routines -> {
                return "Routines"
            }
            Screen.Stats -> {
                return "Statistics"
            }
            Screen.Profile -> {
                return "Profile"
            }
            Screen.AddRoutine -> {
                return "Add Routines"
            }
            else -> {}
        }
        return "Error"
    }

    fun updateSignIn(newState: Boolean) {
        signedIn = newState
    }
}
