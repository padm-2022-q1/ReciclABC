package br.edu.ufabc.reciclabc.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: Long,
    val category: GarbageType,
    val weekdays: List<Weekday>,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean,
): Parcelable
