package br.edu.ufabc.reciclabc.utils.extensions

import android.content.Context
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.model.Weekday

fun weekdayToAbbreviationString(context: Context, weekday: Weekday) =
    when(weekday) {
        Weekday.SUNDAY -> context.resources.getString(R.string.sunday_abbreviation)
        Weekday.MONDAY -> context.resources.getString(R.string.monday_abbreviation)
        Weekday.TUESDAY -> context.resources.getString(R.string.tuesday_abbreviation)
        Weekday.WEDNESDAY -> context.resources.getString(R.string.wednesday_abbreviation)
        Weekday.THURSDAY -> context.resources.getString(R.string.thursday_abbreviation)
        Weekday.FRIDAY -> context.resources.getString(R.string.friday_abbreviation)
        Weekday.SATURDAY -> context.resources.getString(R.string.saturday_abbreviation)
    }

fun weekdaysToAbbreviationString(context: Context, weekdays: List<Weekday>) =
    weekdays.sorted().joinToString(", ") { weekdayToAbbreviationString(context, it) }
