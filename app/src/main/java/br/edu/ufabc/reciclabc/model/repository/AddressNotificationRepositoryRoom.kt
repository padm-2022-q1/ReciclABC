package br.edu.ufabc.reciclabc.model.repository

import android.app.Application
import androidx.room.Room
import androidx.room.withTransaction
import br.edu.ufabc.reciclabc.model.AddressNotification
import br.edu.ufabc.reciclabc.model.room.AppDatabase
import br.edu.ufabc.reciclabc.model.room.entities.AddressEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressNotificationRepositoryRoom(application: Application) {
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(application, AppDatabase::class.java, "ReciclABC").build()
    }

    suspend fun getAll(): List<AddressNotification> = withContext(Dispatchers.IO) {
        db.AddressDao().getAll().map { it.toAddressNotification() }
    }
    suspend fun getById(id: Long): AddressNotification = withContext(Dispatchers.IO) {
        db.AddressDao().getById(id).toAddressNotification()
    }

    suspend fun add(addressNotification: AddressNotification) = withContext(Dispatchers.IO) {
        db.withTransaction {
            val addressId = db.AddressDao().insert(AddressEntity.fromAddressNotification(addressNotification))
            addressNotification.notifications.forEach {
                db.NotificationDao().insert(NotificationEntity.fromNotification(it, addressId))
            }
        }
    }
}
