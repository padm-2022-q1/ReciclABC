package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.model.NotificationGroup

@Entity(foreignKeys = [ForeignKey(entity = AddressEntity::class,
    parentColumns = ["id"],
    childColumns = ["addressId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)])
data class NotificationGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val addressId: Long,
    val category: GarbageType,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean,
) {
    companion object {
        fun fromNotificationGroup(notificationGroup: NotificationGroup, addressId: Long) = NotificationGroupEntity(
            if (notificationGroup.id > 0) notificationGroup.id else 0,
            addressId,
            notificationGroup.category,
            notificationGroup.hours,
            notificationGroup.minutes,
            notificationGroup.isActive,
        )
    }
}
