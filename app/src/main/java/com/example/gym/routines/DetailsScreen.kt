package com.example.gym.routines

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gym.*
import com.example.gym.R
import com.example.gym.database.DatabaseViewModel
import com.example.gym.navigation.NavViewModel
import com.example.gym.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

private const val TAG = "Routine Details Screen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailsScreen(
    routineId: String?,
    repoModel: DatabaseViewModel,
    navController: NavController,
    navModel: NavViewModel,
    firestoreDb: FirebaseFirestore,
    uEmail: String?,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "Recompose")
    var routine: Routine? by remember {
        mutableStateOf(null)
    }
    var editing by remember {
        mutableStateOf(false)
    }
    var numUpdates by remember {
        mutableStateOf(0)
    }
    var addedExistingExercises by remember {
        mutableStateOf(false)
    }
    val exerciseModel: ExerciseViewModel = viewModel()
    val enteredName = remember { mutableStateOf(TextFieldValue("")) }

    val enteredInSearch = remember { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(key1 = numUpdates) {
        if (routineId != null && !routineId.equals("Error")) {
            Log.d(TAG, "Initial ${routineId}")
            routine = repoModel.retrieveRoutineById(routineId.toLong())
            if (!addedExistingExercises) {
                exerciseModel.addAllExercises(routine!!.exercises)
                Log.d(TAG, "Updating enteredName ${routine!!.name}")
                enteredName.value = TextFieldValue(routine!!.name)
                addedExistingExercises = true
            }

        } else {
            routine = null
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledTonalIconButton(
                    onClick = {
                        if (editing) {
                            exerciseModel.updateExercises()
                            if (routineId != null) {
                                Log.d(TAG, routineId)
                            }
                            updateDb(
                                uEmail = uEmail,
                                firestoreDb = firestoreDb,
                                id = routine!!.id,
                                routineName = enteredName.value.text,
                                muscleGroups = routine!!.muscleGroups,
                                exercises = exerciseModel.exercises,
                                repoModel = repoModel
                            )

                            numUpdates += 1
                        }
                        editing = !editing
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Green100,
                        contentColor = Green700
                    ),
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_edit_24),
                        contentDescription = stringResource(R.string.edit_routine),
                    )
                }
                FilledTonalIconButton(
                    onClick = {
                        routine?.let {
                            repoModel.deleteRoutineInDB(it)
                            uEmail?.let { email ->
                                firestoreDb.collection("data").document(email).collection("routine")
                                    .document(it.id.toString())
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
                            navController.navigate(navModel.switchBackToScreen().route)
                        }
                    },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = stringResource(R.string.delete_routine)
                    )
                }
            }
        }
        item {
            Divider(color = Grey200)
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                NameTextField(enteredName = enteredName, enabled = editing, labelText = "Change name")
            }
        }
        item {
            Divider(color = Grey500)
            Text(text = "Exercises")
        }
        if (!editing) {
            exerciseModel.updateExercises()
        }
        routine?.let {
            itemsIndexed(exerciseModel.exercises) { _, exercise ->
                Row(
//                        horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = exercise.name, modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .width(64.dp)
                    )
                    Text(
                        text = formatElementsInOneLine(exercise.muscleGroups),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    if (editing) {
                        IconButton(
                            onClick = {
                                exerciseModel.updateExercises()
//                                        var updatedExercises =
//                                            routine!!.exercises as MutableList<Exercise>
                                val updatedExercises =
                                    exerciseModel.exercises as MutableList<Exercise>
                                updatedExercises.remove(exercise)
                                updateDb(
                                    uEmail = uEmail,
                                    firestoreDb = firestoreDb,
                                    id = routine!!.id,
                                    routineName = routine!!.name,
                                    muscleGroups = routine!!.muscleGroups,
                                    exercises = updatedExercises,
                                    repoModel = repoModel
                                )
                                exerciseModel.removeExercise(exercise)
                                numUpdates += 1
                            },
                            modifier = Modifier.width(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_delete_24),
                                contentDescription = "Remove exercise"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                }
            }
        }
        if (editing) {
            item {
                SearchComponent(
                    enteredInSearch = enteredInSearch,
                    repoModel = repoModel,
                    addExercise = { exercise -> exerciseModel.addExercise(exercise) },
                    removeExercise = { exercise -> exerciseModel.removeExercise(exercise) },
                    alreadyExistingExercises = exerciseModel.exercises.toList()
                )
                Log.d(TAG, "SearchComponent recomposed")
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exerciseModel.clearExercises()
        }
    }
}

fun updateDb(
    uEmail: String?,
    firestoreDb: FirebaseFirestore,
    id: Long,
    routineName: String,
    muscleGroups: List<String>?,
    exercises: List<Exercise>?,
    repoModel: DatabaseViewModel
) {
    if (exercises != null && muscleGroups != null) {
        repoModel.updateRoutineInDB(
            Routine(
                id = id,
                name = routineName,
                exercises = exercises,
                muscleGroups = muscleGroups
            )
        )
        uEmail?.let {
            firestoreDb.collection("data").document(it).collection("routine").document(
                id.toString()
            )
                .update(
                    hashMapOf(
                        "id" to id,
                        "name" to routineName,
                        "exercises" to exercises,
                        "muscleGroups" to muscleGroups
                    )
                )
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }
    }
}


