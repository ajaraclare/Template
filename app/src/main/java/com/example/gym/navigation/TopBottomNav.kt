package com.example.gym.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gym.NavIcon
import com.example.gym.R
import com.example.gym.ui.theme.Green700
import com.example.gym.ui.theme.Green900

object icons {
//    private val screenConst = SCREENCONTANTS
//    val values = listOf(
//        NavIcon(R.drawable.ic_home, R.string.dashboard_description, screenConst.DASHBOARD),
//        NavIcon(R.drawable.ic_schedule, R.string.scheduler_description, screenConst.ROUTINES),
//        NavIcon(R.drawable.ic_analytics, R.string.statistics_description, screenConst.STATS),
//        NavIcon(R.drawable.icon_profile, R.string.profile_description, screenConst.PROFILE)
//    )
    val values = listOf(
        NavIcon(R.drawable.ic_home, R.string.dashboard_description, Screen.Dashboard),
        NavIcon(R.drawable.ic_schedule, R.string.scheduler_description, Screen.Routines),
        NavIcon(R.drawable.ic_analytics, R.string.statistics_description, Screen.Stats),
        NavIcon(R.drawable.icon_profile, R.string.profile_description, Screen.Profile)
    )
}

@Composable
fun GymTrackNavigation(
    navController: NavController,
    switchScreen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Green900,  // sets button clicked animation color
        modifier = modifier
    ) {

        icons.values.map {
            val selected = it.screenVal.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                icon = {
                    Icon(painter = painterResource(it.icon),
                        contentDescription = stringResource(it.info)
                    )
                },
                selectedContentColor = Green900,
                unselectedContentColor = Color.Black,
                selected = selected,
                onClick = {
                    switchScreen(it.screenVal)
                    navController.navigate(it.screenVal.route)
                },
            )
        }
    }
}

@Composable
fun AppBar(
    barText: String,
    backBtnEnabled: Boolean,
    backBtnColor: Color,
    goBackToScreen: () -> Screen,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = barText,
                color = Green700,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(goBackToScreen().route)
            },
                enabled = backBtnEnabled
            ) {
                Icon(painter = painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Toggle Drawer",
                    tint = backBtnColor
                )
            }
        },
        contentColor = Color.Transparent,
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        modifier = modifier
    )
}
//
//    object SCREENCONTANTS {
//        const val DASHBOARD = 0
//        const val ROUTINES = 1
//        const val STATS = 2
//        const val PROFILE = 3
//        const val ROUTINE_DETAILS = 4
//        const val ADD_ROUTINES = 5
//    }