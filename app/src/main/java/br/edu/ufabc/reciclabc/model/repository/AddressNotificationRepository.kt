package br.edu.ufabc.reciclabc.model.repository

import br.edu.ufabc.reciclabc.model.AddressNotification
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Weekday
import com.beust.klaxon.Klaxon
import java.io.InputStream

class AddressNotificationRepository {
    private lateinit var addressNotifications: List<AddressNotification>

    /**
     * Read Json File.
     */
    fun loadData(inputStream: InputStream) {
        addressNotifications = Klaxon().parseArray(inputStream) ?: emptyList()
        addressNotifications =
            addressNotifications.sortedBy { notification -> notification.address }
    }

    fun getAll() = if (this::addressNotifications.isInitialized) addressNotifications
    else throw UninitializedPropertyAccessException("Load data first")

    fun getById(id: Long) =
        if (this::addressNotifications.isInitialized) addressNotifications.find { notification ->
            notification.id == id
        } else throw UninitializedPropertyAccessException("Load data first")

    fun create(
        address: String,
        category: GarbageType,
        hour: Int,
        minute: Int,
        weekdays: List<Weekday>,
    ) {
        // TODO: Create notification
    }
}
