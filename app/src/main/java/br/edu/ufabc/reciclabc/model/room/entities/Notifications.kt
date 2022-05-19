package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.model.NotificationGroup

data class Notifications(
    @Embedded val notificationGroupEntity: NotificationGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val notificationEntities: List<NotificationEntity>,
) {
    fun toNotificationGroup() = NotificationGroup(
        notificationGroupEntity.id,
        notificationGroupEntity.category,
        notificationGroupEntity.hours,
        notificationGroupEntity.minutes,
        notificationGroupEntity.isActive,
        notificationEntities.map { it.toNotification() }
    )
}
