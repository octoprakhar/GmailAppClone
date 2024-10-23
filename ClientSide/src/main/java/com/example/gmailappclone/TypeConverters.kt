package com.example.gmailappclone

import androidx.room.TypeConverter

class Converters {

    // Delimiter used to separate list items in the string
    private val delimiter = ","

    // Convert List<String?> to a single String
    @TypeConverter
    fun fromStringList(value: List<String?>?): String {
        // Use the delimiter to join the list into a single string, replace nulls with a specific placeholder
        return value?.joinToString(delimiter) { it ?: "null" } ?: ""
    }

    // Convert the single String back to List<String?>
    @TypeConverter
    fun toStringList(value: String): List<String?> {
        // If the value is empty, return an empty list
        if (value.isEmpty()) return emptyList()

        // Split the string by the delimiter and convert "null" back to null
        return value.split(delimiter).map { if (it == "null") null else it }
    }
}

