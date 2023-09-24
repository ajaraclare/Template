package com.example.gym.database.converters

import androidx.room.TypeConverter

class IntegerConverters {
    @TypeConverter
    fun fromString(value: String): List<Int> {
        val strings = value.split(",")
        val integers = mutableListOf<Int>()
        strings.forEach {
            integers.add(it.toInt())
        }
        return integers
    }

    @TypeConverter
    fun fromList(list: List<Int>): String {
        return list.joinToString(",")
    }
}