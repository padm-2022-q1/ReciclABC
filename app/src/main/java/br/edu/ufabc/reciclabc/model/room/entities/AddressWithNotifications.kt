package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AddressWithNotifications(
    @Embedded val addressEntity: AddressEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "addressId"
    )
    val notificationEntities: List<NotificationEntity>,
)