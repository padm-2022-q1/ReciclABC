package br.edu.ufabc.reciclabc.ui.notifications.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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

    var currentNotificationId = MutableLiveData<Long?>(null)
    var currentNotificationWeekdays = MutableLiveData<MutableSet<Weekday>>(mutableSetOf())
    var currentNotificationHour = MutableLiveData<Int?>(null)
    var currentNotificationMinute = MutableLiveData<Int?>(null)
    var currentNotificationGarbageType = MutableLiveData(GarbageType.REGULAR)

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
            currentNotificationId.value ?: 0,
            currentNotificationGarbageType.value ?: GarbageType.REGULAR,
            currentNotificationWeekdays.value?.toList() ?: emptyList(),
            currentNotificationHour.value ?: 0,
            currentNotificationMinute.value ?: 0,
            true,
        )

        if (currentNotificationId.value == null) {
            addressNotificationRepository.createNotification(notification, currentAddressId ?: 0)
        } else {
            addressNotificationRepository.updateNotification(notification, currentAddressId ?: 0)
        }

        emit(Result(Unit, Status.Success))
        clearCurrentNotification()
    }

    fun loadNotification(id: Long) = liveData {
        val notification = addressNotificationRepository.getNotification(id)
        currentNotificationId.value = notification.id
        currentNotificationGarbageType.value = notification.category
        currentNotificationHour.value = notification.hours
        currentNotificationMinute.value = notification.minutes
        currentNotificationWeekdays.value = notification.weekdays.toMutableSet()
        emit(Result(Unit, Status.Success))
    }

    private fun clearCurrentNotification() {
        currentNotificationId.value = null
        currentNotificationWeekdays.value = mutableSetOf()
        currentNotificationHour.value = null
        currentNotificationMinute.value = null
        currentNotificationGarbageType.value = GarbageType.REGULAR
    }

    fun deleteNotification(notificationId: Long) {
        // TODO: delete notification
        // addressNotificationRepository.delete(notificationId)
    }
}
