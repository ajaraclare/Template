package com.example.gym.routines

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gym.R
import com.example.gym.database.DatabaseViewModel
import com.example.gym.navigation.NavViewModel
import com.example.gym.navigation.Screen
import com.example.gym.ui.theme.Green700
import com.example.gym.ui.theme.Grey300
import com.example.gym.ui.theme.Grey500
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private const val TAG = "Routines Screen"

@Composable
fun RoutinesScreen(
    navModel: NavViewModel,
    navController: NavController,
    firestoreDb: FirebaseFirestore,
    uEmail: String?,
    repoModel: DatabaseViewModel,
    modifier: Modifier = Modifier,
    muscleModel: MuscleViewModel = viewModel(),
) {
    val routines = repoModel.retrieveRoutinesFromDB().collectAsState(initial = listOf())
    val exercises = repoModel.retrieveExercisesFromDB().collectAsState(initial = listOf())
    val enteredText = remember { mutableStateOf(TextFieldValue("")) }
    var displayingExercises by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(top = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        item {
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
                    ){
                NameTextField(enteredName = enteredText, labelText = "Enter exercise name", modifier = Modifier.width(220.dp))
                ElevatedButton(onClick = {
                    if (enteredText.value.text.isNotEmpty()) {
                        scope.launch {
                            if (repoModel.retrieveExerciseByName(enteredText.value.text) == null) {
                                repoModel.storeExerciseInDB(
                                    enteredText.value.text,
                                    muscleModel.muscles.toList()
                                )
                                uEmail?.let {
                                    firestoreDb.collection("data").document(it)
                                        .collection("exercise").document(enteredText.value.text)
                                        .set(
                                            hashMapOf(
                                                "name" to enteredText.value.text,
                                                "muscleGroups" to muscleModel.muscles.toList()
                                            )
                                        )
                                        .addOnSuccessListener {
                                            Log.d(
                                                TAG,
                                                "DocumentSnapshot successfully written!"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                TAG,
                                                "Error writing document",
                                                e
                                            )
                                        }
                                }
                                Toast.makeText(context, "Exercise Created", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "An exercise with this name already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Unable to create a exercise without a name", Toast.LENGTH_SHORT).show()
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green700,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Create",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

        }
        item {
            Divider(color = Grey300)
            MuscleCheckboxes(addMuscles = { muscle -> muscleModel.addMuscle(muscle) },
                removeMuscles = { muscle -> muscleModel.removeMuscle(muscle) },
            )
            Divider(color = Grey300)
        }
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(onClick = {
                    navModel.switchScreen(Screen.AddRoutine)
                    navController.navigate(Screen.AddRoutine.route)
                }) {
                    Text(
                        text = stringResource(R.string.add_routine),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                OutlinedButton(onClick = {
                    displayingExercises = !displayingExercises
                }) {
                    Text(
                        text = if (displayingExercises) "routines" else "exercises",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        item {
            Divider(color = Grey500)
            if (displayingExercises) {
                if (exercises.value.isEmpty()) {
                    Text(
                        text = "No available exercises",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                    exercises.value.forEachIndexed { _, item ->
                        ExercisesItem(name = item.name, muscleGroups = item.muscleGroups, delete = {
                            repoModel.deleteExerciseInDB(item)
                            uEmail?.let { email ->
                                firestoreDb.collection("data").document(email).collection("exercise")
                                    .document(item.name)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            TAG,
                                            "DocumentSnapshot successfully deleted!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            TAG,
                                            "Error deleting document",
                                            e
                                        )
                                    }
                            }
                        })
                    }
                }
            } else {
                if (routines.value.isEmpty()) {
                    Text(
                        text = "No available routines",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    routines.value.forEachIndexed { _, item ->
                        RoutinesItem(
                            name = item.name,
                            muscleGroups = item.muscleGroups,
                            showDetails = {
                                navModel.switchScreen(Screen.RoutineDetails)
                                navModel.updateTopBarText(item.name)
                                navController.navigate(Screen.RoutineDetails.withArgs(item.id.toString()))
                            }
                        )
                    }
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            muscleModel.clearMuscles()
        }
    }
}

@Composable
fun RoutinesItem(
    name: String,
    muscleGroups: List<String>,
    showDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                showDetails()
            }
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Muscles Targeted: " + formatElementsInOneLine(muscleGroups),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesItem(
    name: String,
    muscleGroups: List<String>,
    delete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
        .fillMaxWidth()) {
        Column(

        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Muscles Targeted: " + formatElementsInOneLine(muscleGroups),
                style = MaterialTheme.typography.bodyMedium
            )

        }
        FilledTonalIconButton(onClick = { delete() }, modifier = Modifier.size(24.dp)) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = stringResource(R.string.delete_routine)
            )
        }
    }
}

fun formatElementsInOneLine(muscleGroups: List<String>): String {
    var resultStr = ""
    for (i in 0..muscleGroups.size-2) {
        resultStr += muscleGroups[i] + ", "
    }
    return resultStr + muscleGroups[muscleGroups.size-1]
}
