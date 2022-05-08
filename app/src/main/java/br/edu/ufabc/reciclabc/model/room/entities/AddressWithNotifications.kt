package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ufabc.reciclabc.model.AddressNotification

data class AddressWithNotifications(
    @Embedded val addressEntity: AddressEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "addressId"
    )
    val notificationEntities: List<NotificationEntity>,
) {
    fun toAddressNotification() = AddressNotification(
        id = addressEntity.id,
        address = addressEntity.address,
        notifications = notificationEntities.map { it.toNotification() }
    )
}