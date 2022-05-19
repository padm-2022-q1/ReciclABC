package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.model.Weekday

@Entity(foreignKeys = [ForeignKey(entity = NotificationGroupEntity::class,
    parentColumns = ["id"],
    childColumns = ["groupId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)])
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val groupId: Long,
    val weekday: Weekday,
) {
    fun toNotification() = Notification(
        id,
        weekday,
    )
    companion object {
        fun fromNotification(notification: Notification, groupId: Long) = NotificationEntity(
            notification.id,
            groupId,
            notification.weekday,
        )
    }
}
