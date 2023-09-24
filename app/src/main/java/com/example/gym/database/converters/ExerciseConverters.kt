package com.example.gym.database.converters

import androidx.room.TypeConverter
import com.example.gym.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExerciseConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<Exercise> {
        val listType = object : TypeToken<List<Exercise>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Exercise>): String {
        return gson.toJson(list)
    }
}