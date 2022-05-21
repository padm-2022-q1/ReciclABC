package br.edu.ufabc.reciclabc.model.repository

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
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

class AddressNotificationRepositoryRoom(application: Application) {

    private val mApplication = application

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
                        if (notificationGroup.isActive) {
                            val context = mApplication.applicationContext
                            val intent = Intent(context, ReminderReceiver::class.java)
                            intent.putExtra("addressId", addressId)
                            intent.putExtra("addressName", address.name)
                            intent.putExtra("garbageType", notificationGroup.category.toString())

                            val pendingIntent = PendingIntent.getBroadcast(context,
                                it.toInt(),
                                intent,
                                PendingIntent.FLAG_IMMUTABLE
                            )

                            ReminderReceiver().setAlarm(context, notificationGroup, notification.weekday.toNumeric(), pendingIntent)
                        }
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
                    it.notifications.forEach { notification ->
                        val context = mApplication.applicationContext
                        val intent = Intent(context, ReminderReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(context,
                            notification.id.toInt(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        ReminderReceiver().cancelAlarm(context, pendingIntent)
                    }
                }
            }
        }
    }

    private fun upsertNotifications(notificationGroup: NotificationGroup, notificationGroupId: Long) {
        val notificationGroupEntity = db.NotificationGroupDao().getById(notificationGroupId)
        val dbNotificationGroup = notificationGroupEntity.toNotificationGroup()
        val address = db.AddressDao().getById(notificationGroupEntity.notificationGroupEntity.addressId).toAddressNotification()

        notificationGroup.notifications.forEach {
            db.NotificationDao().insert(NotificationEntity.fromNotification(it, notificationGroupId)).let { notificationEntityId ->
                if (notificationGroup.isActive) {
                    val context = mApplication.applicationContext
                    val intent = Intent(context, ReminderReceiver::class.java)
                    intent.putExtra("addressId", address.id)
                    intent.putExtra("addressName", address.name)
                    intent.putExtra("garbageType", notificationGroup.category.toString())

                    val notificationId = if (notificationEntityId == -1L) it.id else notificationEntityId
                    val pendingIntent = PendingIntent.getBroadcast(context,
                        notificationId.toInt(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    if (notificationGroup.isActive) {
                        ReminderReceiver().setAlarm(context, notificationGroup, it.weekday.toNumeric(), pendingIntent)
                    } else {
                        ReminderReceiver().cancelAlarm(context, pendingIntent)
                    }
                }
            }

        }

//        If the group is new, there is nothing to delete
        if (notificationGroup.id <= 0L) return

        val dbNotificationsIds = dbNotificationGroup.notifications.map { it.id }.toSet()
        val notificationIds = notificationGroup.notifications.map { it.id }.toSet()

        val deleteNotificationIds = dbNotificationsIds.minus(notificationIds)

        deleteNotificationIds.forEach {
            val notification = dbNotificationGroup.notifications.find { n -> n.id == it }
            db.NotificationDao().delete(NotificationEntity.fromNotification(notification!!, dbNotificationGroup.id))

            val context = mApplication.applicationContext
            val intent = Intent(context, ReminderReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(context,
                it.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            ReminderReceiver().cancelAlarm(context, pendingIntent)
        }
    }

    suspend fun delete(addressId: Long) = withContext(Dispatchers.IO) {
        val address = db.AddressDao().getById(addressId).toAddressNotification()
        address.notifications.forEach { notificationGroup ->
            notificationGroup.notifications.forEach { notification ->
                val context = mApplication.applicationContext
                val intent = Intent(context, ReminderReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context,
                    notification.id.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                ReminderReceiver().cancelAlarm(context, pendingIntent)
            }
        }
        db.AddressDao().delete(AddressEntity.fromAddressNotification(address))
    }

    suspend fun toggleNotification(notificationGroupId: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
        db.NotificationGroupDao().toggleActive(notificationGroupId, isActive)

        val notificationGroupEntity = db.NotificationGroupDao().getById(notificationGroupId)
        val dbNotificationGroup = notificationGroupEntity.toNotificationGroup()
        val address = db.AddressDao().getById(notificationGroupEntity.notificationGroupEntity.addressId).toAddressNotification()
        val context = mApplication.applicationContext

        dbNotificationGroup.notifications.forEach {
            val intent = Intent(context, ReminderReceiver::class.java)
            intent.putExtra("addressId", address.id)
            intent.putExtra("addressName", address.name)
            intent.putExtra("garbageType", dbNotificationGroup.category.toString())
            val pendingIntent = PendingIntent.getBroadcast(context,
                it.id.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            if (isActive) {
                ReminderReceiver().setAlarm(context, dbNotificationGroup, it.weekday.toNumeric(), pendingIntent)
            } else {
                ReminderReceiver().cancelAlarm(context, pendingIntent)
            }
        }
    }
}
