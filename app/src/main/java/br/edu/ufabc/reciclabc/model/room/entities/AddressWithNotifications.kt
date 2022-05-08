package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AddressWithNotifications(
    @Embedded val address: Address,
    @Relation(
        parentColumn = "id",
        entityColumn = "addressId"
    )
    val notifications: List<Notification>,
)
