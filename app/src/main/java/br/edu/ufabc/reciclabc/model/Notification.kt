package br.edu.ufabc.reciclabc.model

data class Notification(
    val id: Long,
    val category: GarbageType,
    val weekdays: List<Weekday>,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean,
)
