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

    var currentAddressId = MutableLiveData<Long?>(null)
    var currentAddressName = MutableLiveData("")
    var currentNotificationList = MutableLiveData<MutableList<Notification>>(mutableListOf())

    var currentNotificationId = MutableLiveData<Long?>(null)
    var currentNotificationWeekdays = MutableLiveData<MutableSet<Weekday>>(mutableSetOf())
    var currentNotificationHour = MutableLiveData<Int?>(null)
    var currentNotificationMinute = MutableLiveData<Int?>(null)
    var currentNotificationGarbageType = MutableLiveData(GarbageType.REGULAR)

    fun loadAddress(id: Long) = liveData {
        val address = addressNotificationRepository.getAddressById(id)
        currentAddressId.value = address.id
        currentAddressName.value = address.name
        currentNotificationList.value = address.notifications.toMutableList()
        emit(Result(Unit, Status.Success))
    }

    fun saveAddress() = liveData {
        try {
            val address = Address(
                currentAddressId.value ?: 0,
                currentAddressName.value ?: "",
                currentNotificationList.value ?: emptyList()
            )

            if (currentAddressId.value == null) {
                addressNotificationRepository.createAddress(address)
            } else {
                addressNotificationRepository.updateAddress(address)
            }

            emit(Result(Unit, Status.Success))
        } catch (e: Exception) {
            emit(Result(Unit, Status.Error(Exception("something went wrong"))))
        }
    }

    fun loadNotification(id: Long) = liveData {
        val notification = addressNotificationRepository.getNotificationById(id)
        currentNotificationId.value = notification.id
        currentNotificationGarbageType.value = notification.category
        currentNotificationHour.value = notification.hours
        currentNotificationMinute.value = notification.minutes
        currentNotificationWeekdays.value = notification.weekdays.toMutableSet()
        emit(Result(Unit, Status.Success))
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
            currentNotificationList.value?.add(notification)
        } else {
            currentNotificationList.value =
                currentNotificationList.value?.map {
                    if (it.id == currentNotificationId.value) notification else it
                }?.toMutableList()
        }

        emit(Result(Unit, Status.Success))
        clearCurrentNotification()
        currentAddressId.value?.let { loadAddress(it) }
    }

    fun deleteNotification(notificationId: Long) {
        currentNotificationList.value =
            currentNotificationList.value?.filter { it.id != notificationId }?.toMutableList()
    }

    private fun clearCurrentNotification() {
        currentNotificationId.value = null
        currentNotificationWeekdays.value = mutableSetOf()
        currentNotificationHour.value = null
        currentNotificationMinute.value = null
        currentNotificationGarbageType.value = GarbageType.REGULAR
    }
}
