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
import br.edu.ufabc.reciclabc.ui.shared.Status
import br.edu.ufabc.reciclabc.ui.shared.Result

class AddressDetailsViewModel(application: Application) : AndroidViewModel(application) {
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
        if (currentAddressId.value == null) {
            try {
                val address = addressNotificationRepository.getById(id)
                currentAddressId.value = address.id
                currentAddressName.value = address.name
                currentNotificationList.value = address.notifications.toMutableList()
                emit(Result(Unit, Status.Success))
            } catch (e: Exception) {
                emit(Result(Unit, Status.Error(Exception("failed to load address notification"))))
            }
        }
    }

    fun saveAddress() = liveData {
        try {
            val address = Address(
                currentAddressId.value ?: 0,
                currentAddressName.value ?: "",
                currentNotificationList.value ?: emptyList()
            )

            if (currentAddressId.value == null) {
                addressNotificationRepository.add(address)
            } else {
                addressNotificationRepository.update(address)
            }

            emit(Result(Unit, Status.Success))
        } catch (e: Exception) {
            emit(Result(Unit, Status.Error(Exception("failed to save address notification"))))
        }
    }

    fun loadNotification(id: Long) = liveData {
        try {
            currentNotificationList.value?.let {
                it.find { notification -> notification.id == id }?.let { notification ->
                    currentNotificationId.value = notification.id
                    currentNotificationGarbageType.value = notification.category
                    currentNotificationHour.value = notification.hours
                    currentNotificationMinute.value = notification.minutes
                    currentNotificationWeekdays.value = notification.weekdays.toMutableSet()
                    emit(Result(Unit, Status.Success))
                }
            }
        } catch (e: Exception) {
            emit(Result(Unit, Status.Error(Exception("failed to load notification"))))
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

    fun toggleNotification(notificationId: Long, isActive: Boolean) {
        currentNotificationList.value?.first { it.id == notificationId }?.let {
            val notification = Notification(it.id, it.category, it.weekdays, it.hours, it.minutes, isActive)

            currentNotificationList.value = currentNotificationList.value?.map { n ->
                if (n.id == notificationId) notification else n
            }?.toMutableList()
        }
    }

    fun deleteNotification(notificationId: Long) {
        currentNotificationList.value =
            currentNotificationList.value?.filter { it.id != notificationId }?.toMutableList()
    }

    fun clearCurrentNotification() {
        currentNotificationId.value = null
        currentNotificationWeekdays.value = mutableSetOf()
        currentNotificationHour.value = null
        currentNotificationMinute.value = null
        currentNotificationGarbageType.value = GarbageType.REGULAR
    }
}
