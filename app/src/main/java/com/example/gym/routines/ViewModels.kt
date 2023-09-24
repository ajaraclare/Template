package com.example.gym.routines

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.gym.Exercise

class MuscleViewModel: ViewModel() {
    private val _muscles: SnapshotStateList<String> = mutableStateListOf()
    val muscles: List<String>
        get() = _muscles

    fun addMuscle(muscle: String) {
        _muscles.add(muscle)
    }
    fun removeMuscle(muscle: String) {
        _muscles.remove(muscle)
    }
    fun clearMuscles() {
        _muscles.clear()
    }
}
class ExerciseViewModel: ViewModel() {
    private var _exercises = mutableStateListOf<Exercise>()
    val exercises: List<Exercise>
        get() = _exercises

    private var _selectedExercises = mutableListOf<Exercise>()
    val selectedExercises: List<Exercise>
        get() = _selectedExercises
    fun addExercise(exercise: Exercise) {
        _selectedExercises.add(exercise)
    }
    fun addAllExercises(exercises: List<Exercise>) {
        _exercises.addAll(exercises)
    }
    fun removeExercise(exercise: Exercise) {
        _selectedExercises.remove(exercise)
    }
    fun clearExercises() {
        _selectedExercises.clear()
        _exercises.clear()
    }
    fun updateExercises() {
        _exercises.addAll(selectedExercises)

        val uniqueSet = _exercises.toSet()
        _exercises = uniqueSet.toMutableStateList()
        _selectedExercises.clear()
    }
}