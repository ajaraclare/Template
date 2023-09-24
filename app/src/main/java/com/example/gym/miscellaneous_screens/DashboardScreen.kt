package com.example.gym

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.gym.database.DatabaseViewModel
import com.example.gym.routines.formatElementsInOneLine
import com.example.gym.ui.theme.Grey500
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repoModel: DatabaseViewModel,
    firestoreDb: FirebaseFirestore,
    uEmail: String?,
    modifier: Modifier = Modifier
) {
    val routines = repoModel.retrieveRoutinesFromDB().collectAsState(initial = listOf())
    var clicked by remember {
        mutableStateOf(false)
    }
    var selectedRoutine: Routine? by remember {
        mutableStateOf(null)
    }
    val sessions = repoModel.retrieveMostRecentSessionEntriesFromDB().collectAsState(initial = listOf())
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(text = "Choose a routine", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            if (routines.value.isEmpty()) {
                Text(text = "No available routines", style = MaterialTheme.typography.headlineSmall)
            }
            routines.value.forEachIndexed { _, routine ->
                OutlinedCard(onClick = {
                    if (!clicked) {
                        clicked = true
                        selectedRoutine = routine
                    } else if (selectedRoutine == routine) {
                        selectedRoutine = null
                        clicked = false
                    }
                }) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Box (
                            modifier = Modifier.fillMaxWidth(0.2f)
                        ){
                            Text(text = routine.name, style = MaterialTheme.typography.bodyMedium)
                        }
                        Box (
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ){
                            Text(text = formatElementsInOneLine(routine.muscleGroups))
                        }
                    }
                }
            }
        }
        item {
            if (clicked) {
                selectedRoutine?.let { routine ->
                    Column() {
                        Divider(color = Grey500)
                        val textFields = remember { (List(routine.exercises.size) {TextFieldValue("")}).toMutableStateList() }
//                        var textFields = mutableListOf<TextFieldValue>()
//                        remember {
//                            textFields.addAll(List(it.exercises.size) { TextFieldValue("") })
//                        }
                        routine.exercises.forEachIndexed { index, exercise ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
//                                var enteredCount = remember { mutableStateOf(TextFieldValue("")) }
                                Text(text = exercise.name, modifier = Modifier.width(196.dp), style = MaterialTheme.typography.headlineSmall)
                                OutlinedTextField(
                                    value = textFields[index].text,
                                    onValueChange = { currentEntered ->
                                        textFields[index] = TextFieldValue(currentEntered)
                                    },
                                    label = {
                                            Text(text = "Enter rep count for this exercise")
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                )
                            }
                        }
                        ElevatedButton(onClick = {
                            routine.exercises.forEachIndexed { index, exercise ->
                                Log.d("Dashboard Screen", "Exercise: ${exercise.name}, Rep count: ${textFields[index].text}")
                            }
                            val repCounts = mutableListOf<Int>()
                            val dateTime = CustomDateTime()
                            textFields.forEach { textFieldValue ->
                                if (textFieldValue.text.equals("")) {
                                    repCounts.add(0)
                                } else {
                                    repCounts.add(textFieldValue.text.toDouble().roundToInt())
                                }
                            }
                            val sessionEntry = SessionEntry(routine.name, repCounts = repCounts, dateCreated = dateTime)
                            uEmail?.let { email ->
                                firestoreDb.collection("data").document(email).collection("entry").document(dateTime.toString())
                                    .set(
//                                        hashMapOf(
//                                            "routineName" to routine.name,
//                                            "routineId" to routine.id,
//                                            "repCounts" to repCounts,
//                                            "datetime" to dateTime
//                                        )
                                        sessionEntry
                                    )
                                repoModel.storeSessionEntryInDB(
                                    sessionEntry
                                )
                                selectedRoutine = null
                                clicked = false
                            }
                        },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                            Text(text = "Submit")
                        }
                    }
                }
            }
        }
        item {
            Divider(color = Grey500)
            Text(text = "Last Sessions", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            if (sessions.value.isEmpty()) {
                Text(text = "No available sessions", style = MaterialTheme.typography.headlineSmall)
            }
        }
        itemsIndexed(sessions.value) { index, session ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        Text(text = session.routineName, style = MaterialTheme.typography.headlineSmall)
                    }
                    Box {
                        Text(text = "${session.dateCreated.value.month.name.toLowerCase().capitalize()} ${session.dateCreated.value.dayOfMonth}",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    session.repCounts.forEach {
                        Box {
                            Text(text = it.toString(), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

}