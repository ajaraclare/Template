package com.example.gym.database.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateConverters {
    companion object {
        val formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS")
    }

    @TypeConverter
    fun fromString(value: String): LocalDateTime {
        return LocalDateTime.parse(value, formatter)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDateTime): String {
        return date.format(formatter)
    }
}