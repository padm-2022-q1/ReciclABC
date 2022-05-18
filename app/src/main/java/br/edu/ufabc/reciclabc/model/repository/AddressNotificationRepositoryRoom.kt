package br.edu.ufabc.reciclabc.model.repository

import android.app.Application
import androidx.room.Room
import androidx.room.withTransaction
import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.model.room.AppDatabase
import br.edu.ufabc.reciclabc.model.room.entities.AddressEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressNotificationRepositoryRoom(application: Application) {
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
            address.notifications.forEach {
                db.NotificationDao().insert(NotificationEntity.fromNotification(it, addressId))
            }
        }
    }

    suspend fun update(address: Address) = withContext(Dispatchers.IO) {
        val dbAddress = db.AddressDao().getById(address.id).toAddressNotification()
        val dbNotificationsIds = dbAddress.notifications.map { it.id }.toSet()
        val notificationIds = address.notifications.map { it.id }.toSet()

        val deleteNotificationIds = dbNotificationsIds.minus(notificationIds)

        db.withTransaction {
            db.AddressDao().update(AddressEntity.fromAddressNotification(address))
            address.notifications.forEach {
                db.NotificationDao().upsert(NotificationEntity.fromNotification(it, dbAddress.id))
            }

            deleteNotificationIds.forEach {
                dbAddress.notifications.find { n -> n.id == it }?.let {
                    db.NotificationDao().delete(NotificationEntity.fromNotification(it, dbAddress.id))
                }
            }
        }
    }

    suspend fun delete(addressId: Long) = withContext(Dispatchers.IO) {
        val address = db.AddressDao().getById(addressId).toAddressNotification()
        db.withTransaction {
            db.AddressDao().delete(AddressEntity.fromAddressNotification(address))
        }
    }

    suspend fun toggleNotification(notificationId: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
            db.NotificationDao().toggleActive(notificationId, isActive)
    }
}
