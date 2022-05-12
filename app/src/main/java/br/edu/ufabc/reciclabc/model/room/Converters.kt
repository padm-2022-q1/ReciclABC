package br.edu.ufabc.reciclabc.model.room

import androidx.room.TypeConverter
import br.edu.ufabc.reciclabc.model.Weekday

/**
 * Converter based on https://stackoverflow.com/a/58027206
 */
class Converters {
    @TypeConverter
    fun storedStringToWeekdayList(str: String) =
        str.split(",").map { Weekday.valueOf(it.trim()) }

    @TypeConverter
    fun weekdayListToStoredString(weekdays: List<Weekday>) =
        weekdays.joinToString(",") { weekday -> weekday.name }
}
