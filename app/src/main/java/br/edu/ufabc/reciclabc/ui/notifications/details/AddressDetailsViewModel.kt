package br.edu.ufabc.reciclabc.ui.notifications.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import br.edu.ufabc.reciclabc.App
import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.model.Weekday

class AddressDetailsViewModel(application: Application) : AndroidViewModel(application) {

    sealed class Status {
        class Error(val e: Exception) : Status()
        object Success : Status()
    }

    data class Result<T>(
        val result: T?,
        val status: Status,
    )

    private val addressNotificationRepository = (application as App).addressNotificationRepository

    var currentAddressId: Long? = null
    var currentAddressName = ""
    var currentNotificationList: List<Notification> = mutableListOf()

    var currentNotificationId: Long? = null
    var currentNotificationWeekdays = mutableSetOf<Weekday>()
    var currentNotificationHour: Int? = null
    var currentNotificationMinute: Int? = null
    var currentNotificationGarbageType = GarbageType.REGULAR

    fun saveAddress() = liveData {
        try {
            val address = Address(
                currentAddressId ?: 0,
                currentAddressName,
                currentNotificationList
            )

            if (currentAddressId != null) {
                addressNotificationRepository.createAddress(address)
            } else {
                addressNotificationRepository.updateAddress(address)
            }

            emit(Result(Unit, Status.Success))
        } catch (e: Exception) {
            emit(false)
            emit(Result(Unit, Status.Error(Exception("something went wrong"))))
        }
    }

    fun saveNotification() = liveData {
        val notification = Notification(
            currentNotificationId ?: 0,
            currentNotificationGarbageType,
            currentNotificationWeekdays.toList(),
            currentNotificationHour ?: 0,
            currentNotificationMinute ?: 0,
            true,
        )

        if (currentNotificationId == null) {
            addressNotificationRepository.createNotification(notification, currentAddressId ?: 0)
        } else {
            addressNotificationRepository.updateNotification(notification, currentAddressId ?: 0)
        }

        emit(Result(Unit, Status.Success))
        clearCurrentNotification()
    }

    fun loadNotification(id: Long) {
        val notification = addressNotificationRepository.getNotification(id)
        currentNotificationId = notification.id
        currentNotificationGarbageType = notification.category
        currentNotificationHour = notification.hours
        currentNotificationMinute = notification.minutes
        currentNotificationWeekdays = notification.weekdays.toMutableSet()
    }

    private fun clearCurrentNotification() {
        currentNotificationId = null
        currentNotificationWeekdays = mutableSetOf()
        currentNotificationHour = null
        currentNotificationMinute = null
        currentNotificationGarbageType = GarbageType.REGULAR
    }

    fun deleteNotification(notificationId: Long) {
        // TODO: delete notification
        // addressNotificationRepository.delete(notificationId)
    }
}
