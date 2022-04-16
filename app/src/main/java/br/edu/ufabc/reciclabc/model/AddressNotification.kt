package br.edu.ufabc.reciclabc.model

data class AddressNotification(
    val id: Long,
    val address: String,
    val regularGarbage: List<Notification>,
    val recyclableGarbage: List<Notification>
)
