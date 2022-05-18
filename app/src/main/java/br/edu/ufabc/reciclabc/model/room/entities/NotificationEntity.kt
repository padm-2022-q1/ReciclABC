package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.model.Weekday

@Entity(foreignKeys = [ForeignKey(entity = AddressEntity::class,
    parentColumns = ["id"],
    childColumns = ["addressId"],
    onDelete = ForeignKey.CASCADE)])
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val addressId: Long,
    val category: GarbageType,
    val weekdays: List<Weekday>,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean,
) {
    fun toNotification() = Notification(
        id,
        category,
        weekdays,
        hours,
        minutes,
        isActive,
    )
    companion object {
        fun fromNotification(notification: Notification, addressId: Long) = NotificationEntity(
            if (notification.id < 0) 0 else notification.id,
            addressId,
            notification.category,
            notification.weekdays,
            notification.hours,
            notification.minutes,
            notification.isActive,
        )
    }
}
