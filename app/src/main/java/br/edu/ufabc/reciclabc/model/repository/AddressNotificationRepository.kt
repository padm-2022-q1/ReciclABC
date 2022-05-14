package br.edu.ufabc.reciclabc.model.repository

import android.util.Log
import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.model.Weekday
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class AddressNotificationRepository {
    private lateinit var addresses: List<Address>

    /**
     * Read Json File.
     */
    fun loadData(inputStream: InputStream) {
        addresses = Klaxon().parseArray(inputStream) ?: emptyList()
        addresses =
            addresses.sortedBy { notification -> notification.name }
    }

    fun getAll() = if (this::addresses.isInitialized) addresses
    else throw UninitializedPropertyAccessException("Load data first")

    suspend fun getById(id: Long) =
        if (this::addresses.isInitialized) addresses.find { notification ->
            notification.id == id
        } else throw UninitializedPropertyAccessException("Load data first")

    suspend fun createAddress(
        address: Address,
    ): Long {
        TODO("Create address")
    }

    suspend fun updateAddress(
        address: Address,
    ) {
        TODO("Update address")
    }

    suspend fun getNotification(id: Long) : Notification = withContext(Dispatchers.IO) {
        Notification(id, GarbageType.RECYCLABLE, listOf(Weekday.FRIDAY), 15, 0, true)
    }

    suspend fun createNotification(notification: Notification, addressId: Long): Long {
        Log.d("REPO", notification.toString())
        return 0
        TODO("Create notification")
    }

    suspend fun updateNotification(notification: Notification, addressId: Long) {
        Log.d("REPO", notification.toString())
        return
        TODO("Update notification")
    }
}
