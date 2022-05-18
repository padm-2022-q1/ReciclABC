package br.edu.ufabc.reciclabc.model

import android.os.Parcelable

data class NotificationGroup(
    val id: Long,
    val category: GarbageType,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean,
    val notifications: List<Notification>,
) {
    fun getWeekDays() = notifications.map {
        it.weekday
    }
}
