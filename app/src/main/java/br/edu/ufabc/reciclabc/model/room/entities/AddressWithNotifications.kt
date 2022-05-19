package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ufabc.reciclabc.model.Address

data class AddressWithNotifications(
    @Embedded val addressEntity: AddressEntity,
    @Relation(
        entity = NotificationGroupEntity::class,
        parentColumn = "id",
        entityColumn = "addressId"
    )
    val notifications: List<Notifications>,
) {
    fun toAddressNotification() = Address(
        addressEntity.id,
        addressEntity.address,
        notifications.map { it.toNotificationGroup() }
    )
}
