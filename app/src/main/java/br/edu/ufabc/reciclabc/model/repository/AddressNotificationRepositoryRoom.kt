package br.edu.ufabc.reciclabc.model.repository

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import androidx.room.withTransaction
import br.edu.ufabc.reciclabc.ReminderReceiver
import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.model.NotificationGroup
import br.edu.ufabc.reciclabc.model.room.AppDatabase
import br.edu.ufabc.reciclabc.model.room.entities.AddressEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationGroupEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressNotificationRepositoryRoom(application: Application): AndroidViewModel(application) {
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(application, AppDatabase::class.java, "ReciclABC").build()
    }

    suspend fun getAll(): List<Address> = withContext(Dispatchers.IO) {
        db.AddressDao().getAll().map { it.toAddressNotification() }
    }
    suspend fun getById(id: Long): Address = withContext(Dispatchers.IO) {
        db.AddressDao().getById(id).toAddressNotification()
    }

    suspend fun add(address: Address) = withContext(Dispatchers.IO) {
        db.withTransaction {
            val addressId = db.AddressDao().insert(AddressEntity.fromAddressNotification(address))
            address.notifications.forEach { notificationGroup ->
                val groupId = db.NotificationGroupDao().insert(NotificationGroupEntity.fromNotificationGroup(notificationGroup, addressId))
                notificationGroup.notifications.forEach { notification ->
                    db.NotificationDao().insert(NotificationEntity.fromNotification(notification, groupId)).let {
                        val context = getApplication<Application?>().applicationContext
                        val intent = Intent(context, ReminderReceiver::class.java)
                        intent.putExtra("addressId", notificationGroup.id)
                        intent.putExtra("addressId", notificationGroup.category)

                        val pendingIntent = PendingIntent.getBroadcast(context,
                            it.toInt(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        ReminderReceiver().setAlarm(context, notificationGroup, (notification.weekday.ordinal%7)+1, pendingIntent)
                    }
                }
            }
        }
    }

    suspend fun update(address: Address) = withContext(Dispatchers.IO) {
        val dbAddress = db.AddressDao().getById(address.id).toAddressNotification()
        val dbNotificationsIds = dbAddress.notifications.map { it.id }.toSet()
        val notificationIds = address.notifications.map { it.id }.toSet()

        val deleteNotificationGroupIds = dbNotificationsIds.minus(notificationIds)

        db.withTransaction {
            db.AddressDao().update(AddressEntity.fromAddressNotification(address))
            address.notifications.forEach {
                val notificationGroupId = db.NotificationGroupDao().upsert(NotificationGroupEntity.fromNotificationGroup(it, dbAddress.id))
                upsertNotifications(it, notificationGroupId)
            }

            deleteNotificationGroupIds.forEach {
                dbAddress.notifications.find { ng -> ng.id == it }?.let {
                    db.NotificationGroupDao().delete(NotificationGroupEntity.fromNotificationGroup(it, dbAddress.id))
                }
            }
        }
    }

    private fun upsertNotifications(notificationGroup: NotificationGroup, notificationGroupId: Long) {
        val dbNotificationGroup = db.NotificationGroupDao().getById(notificationGroupId).toNotificationGroup()

        notificationGroup.notifications.forEach {
            db.NotificationDao().insert(NotificationEntity.fromNotification(it, notificationGroupId))
        }

//        If the group is new, there is nothing to delete
        if (notificationGroup.id <= 0L) return

        val dbNotificationsIds = dbNotificationGroup.notifications.map { it.id }.toSet()
        val notificationIds = notificationGroup.notifications.map { it.id }.toSet()

        val deleteNotificationIds = dbNotificationsIds.minus(notificationIds)

        deleteNotificationIds.forEach {
            val notification = dbNotificationGroup.notifications.find { n -> n.id == it }
            db.NotificationDao().delete(NotificationEntity.fromNotification(notification!!, dbNotificationGroup.id))
        }
    }

    suspend fun delete(addressId: Long) = withContext(Dispatchers.IO) {
        val address = db.AddressDao().getById(addressId).toAddressNotification()
        db.AddressDao().delete(AddressEntity.fromAddressNotification(address))
    }

    suspend fun toggleNotification(notificationGroupId: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
        db.NotificationGroupDao().toggleActive(notificationGroupId, isActive)
    }
}
