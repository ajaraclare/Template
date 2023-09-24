package com.example.gym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gym.database.DatabaseViewModel
import com.example.gym.miscellaneous_screens.StatisticsScreen
import com.example.gym.database.TrackerRepository
import com.example.gym.miscellaneous_screens.SignInScreen
import com.example.gym.miscellaneous_screens.SignUpScreen
import com.example.gym.navigation.*
import com.example.gym.routines.AddRoutinesScreen
import com.example.gym.routines.RoutineDetailsScreen
import com.example.gym.routines.RoutinesScreen
import com.example.gym.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

private const val TAG = "Main Activity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var trackerRepo: TrackerRepository
    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            signedIn = true
        }
    }

    private lateinit var auth: FirebaseAuth
    private var signedIn = false

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val firestoreDb = Firebase.firestore

        setContent {
            GymTheme {
                val navModel: NavViewModel = viewModel()
                val navController = rememberNavController()
                val databaseModel: DatabaseViewModel = viewModel()
                var mod: Modifier = Modifier.fillMaxSize()

                LaunchedEffect(Unit) {
                    navModel.updateSignIn(signedIn)
                }

                if (!navModel.signedIn) {
                    NavHost(navController = navController, startDestination = Screen.SignIn.route) {
                        composable(route = Screen.SignIn.route) {
                            SignInScreen(auth = auth, navController = navController, navModel = navModel,
                                firestoreDb = firestoreDb, repoModel = databaseModel,
                                modifier = mod)
                        }
                        composable(route = Screen.SignUp.route) {
                            SignUpScreen(auth = auth, navController = navController, mod)
                        }
                    }

//                    SignInScreen(auth = auth, navController = navController, mod)
                } else {
//                    navModel.updateSignIn(true)
                    Scaffold(
                        bottomBar = {
                            GymTrackNavigation(
                                switchScreen = { screen -> navModel.switchScreen(screen) },
                                navController = navController
                            )
                        },
                        topBar = {
                            AppBar(
                                barText = navModel.appBarText,
                                backBtnEnabled = navModel.backButtonEnabled,
                                backBtnColor = navModel.backButtonColor,
                                goBackToScreen = { navModel.switchBackToScreen() },
                                navController = navController
                            )
                        }
                    ) { paddingValues ->
                        mod = mod
                            .padding(paddingValues)
                        NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
//                            composable(route = Screen.SingIn.route) {
//                                SignInScreen(auth = auth, navController = navController, navModel = navModel, mod)
////                                SignInScreen(auth = auth, navController = navController, mod)
//                            }
                            composable(route = Screen.Dashboard.route) {
                                DashboardScreen(repoModel = databaseModel, firestoreDb = firestoreDb,
                                    uEmail = auth.currentUser?.email, mod)
                            }
                            composable(route = Screen.Profile.route) {
                                ProfileScreen(auth = auth, navModel = navModel, mod)
                            }
                            composable(route = Screen.Stats.route) {
                                StatisticsScreen(repoModel = databaseModel, modifier = mod)
                            }
                            composable(route = Screen.Routines.route) {
                                RoutinesScreen(navModel = navModel, navController = navController, repoModel = databaseModel,
                                    firestoreDb = firestoreDb, uEmail = auth.currentUser?.email,
                                    modifier = mod)
                            }
                            composable(
//                                route = Screen.RoutineDetails.route + "/{detailName}",
//                                arguments = listOf(
//                                    navArgument("detailName") {
//                                        type = NavType.StringType
//                                        defaultValue = "Error"
//                                        nullable = true
//                                    }
//                                )
                                route = Screen.RoutineDetails.route + "/{routineId}",
                                arguments = listOf(
                                    navArgument("routineId") {
                                        type = NavType.StringType
                                        defaultValue = "Error"
                                    }
                                )
                            ) { entry ->
//                                RoutineDetailsScreen(detailName = entry.arguments?.getString("detailName"),
//                                    repoModel = databaseModel, navController = navController, navModel = navModel,
//                                    firestoreDb = firestoreDb, uEmail = auth.currentUser?.email, modifier =mod)
                                RoutineDetailsScreen(routineId = entry.arguments!!.getString("routineId"),
                                    repoModel = databaseModel, navController = navController, navModel = navModel,
                                    firestoreDb = firestoreDb, uEmail = auth.currentUser?.email, modifier =mod)
                            }
                            composable(route = Screen.AddRoutine.route) {
                                AddRoutinesScreen(context = this@MainActivity, repoModel = databaseModel,
                                    firestoreDb = firestoreDb, uEmail = auth.currentUser?.email,
                                    modifier = mod)
                            }
                        }
                    }
                }
            }
        }
    }
}

