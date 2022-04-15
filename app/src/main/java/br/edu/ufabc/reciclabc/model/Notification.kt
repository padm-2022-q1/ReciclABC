package br.edu.ufabc.reciclabc.model

data class Notification(
    val id: Long,
    val weekday: String,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean
)
