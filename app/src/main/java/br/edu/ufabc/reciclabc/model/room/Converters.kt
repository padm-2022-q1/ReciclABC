package br.edu.ufabc.reciclabc.model.room

import androidx.room.TypeConverter
import br.edu.ufabc.reciclabc.model.Weekday

/**
 * Converter based on https://stackoverflow.com/a/58027206
 */
class Converters {
    @TypeConverter
    fun storedStringToWeekdayList(str: String): List<Weekday> {
        val dbValues = str.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }
        val enums: MutableList<Weekday> = ArrayList()

        for (s in dbValues)
            enums.add(Weekday.valueOf(s))

        return enums
    }

    @TypeConverter
    fun weekdayListToStoredString(weekdays: List<Weekday>): String {
        var str = ""

        for (weekday in weekdays)
            str += weekday.name + ","

        return str
    }
}