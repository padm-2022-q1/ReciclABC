package br.edu.ufabc.reciclabc.ui.notifications.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import br.edu.ufabc.reciclabc.App
import br.edu.ufabc.reciclabc.model.*
import br.edu.ufabc.reciclabc.ui.shared.Result
import br.edu.ufabc.reciclabc.ui.shared.Status

class AddressDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val addressNotificationRepository = (application as App).addressNotificationRepository

    var currentAddressId = MutableLiveData<Long?>(null)
    var currentAddressName = MutableLiveData("")
    var currentNotificationGroupList = MutableLiveData<MutableList<NotificationGroup>>(mutableListOf())

    var currentNotificationGroupId = MutableLiveData<Long?>(null)
    var currentNotificationGroupWeekdays = MutableLiveData<MutableSet<Weekday>>(mutableSetOf())
    var currentNotificationGroupHour = MutableLiveData<Int?>(null)
    var currentNotificationGroupMinute = MutableLiveData<Int?>(null)
    var currentNotificationGroupGarbageType = MutableLiveData(GarbageType.REGULAR)
    var currentNotificationGroupNotifications = MutableLiveData<MutableList<Notification>>(mutableListOf())

    var generatedId = 0L

    fun loadAddress(id: Long) = liveData {
        if (currentAddressId.value == null) {
            try {
                val address = addressNotificationRepository.getById(id)
                currentAddressId.value = address.id
                currentAddressName.value = address.name
                currentNotificationGroupList.value = address.notifications.toMutableList()
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
                currentNotificationGroupList.value ?: emptyList()
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

    fun loadNotificationGroup(id: Long) = liveData {
        try {
            currentNotificationGroupList.value?.let {
                it.find { notificationGroup -> notificationGroup.id == id }?.let { notificationGroup ->
                    currentNotificationGroupId.value = notificationGroup.id
                    currentNotificationGroupGarbageType.value = notificationGroup.category
                    currentNotificationGroupHour.value = notificationGroup.hours
                    currentNotificationGroupMinute.value = notificationGroup.minutes
                    currentNotificationGroupWeekdays.value = notificationGroup.getWeekDays().toMutableSet()
                    currentNotificationGroupNotifications.value = notificationGroup.notifications.toMutableList()
                    emit(Result(Unit, Status.Success))
                }
            }
        } catch (e: Exception) {
            emit(Result(Unit, Status.Error(Exception("failed to load notification"))))
        }
    }

    fun saveNotificationGroup() = liveData {
        val notificationList = currentNotificationGroupWeekdays.value?.map { weekday ->
            if (currentNotificationGroupNotifications.value?.map { it.weekday }!!.contains(weekday))
                currentNotificationGroupNotifications.value?.find { notification -> notification.weekday == weekday }!!
            else
                Notification(0, weekday)
        } ?:emptyList()
        val notificationGroup = NotificationGroup(
            currentNotificationGroupId.value ?: --generatedId,
            currentNotificationGroupGarbageType.value ?: GarbageType.REGULAR,
            currentNotificationGroupHour.value ?: 0,
            currentNotificationGroupMinute.value ?: 0,
            true,
            notificationList,
        )

        if (currentNotificationGroupId.value == null) {
            currentNotificationGroupList.value?.add(notificationGroup)
        } else {
            currentNotificationGroupList.value =
                currentNotificationGroupList.value?.map {
                    if (it.id == currentNotificationGroupId.value) notificationGroup else it
                }?.toMutableList()
        }

        emit(Result(Unit, Status.Success))
        clearCurrentNotificationGroup()
        currentAddressId.value?.let { loadAddress(it) }
    }

    fun toggleNotification(notificationGroupId: Long, isActive: Boolean) {
        currentNotificationGroupList.value?.first { it.id == notificationGroupId }?.let {
            val notification = NotificationGroup(it.id, it.category, it.hours, it.minutes, isActive, it.notifications)

            currentNotificationGroupList.value = currentNotificationGroupList.value?.map { n ->
                if (n.id == notificationGroupId) notification else n
            }?.toMutableList()
        }
    }

    fun deleteNotificationGroup(notificationGroupId: Long) {
        currentNotificationGroupList.value =
            currentNotificationGroupList.value?.filter { it.id != notificationGroupId }?.toMutableList()
    }

    fun clearCurrentNotificationGroup() {
        currentNotificationGroupId.value = null
        currentNotificationGroupWeekdays.value = mutableSetOf()
        currentNotificationGroupHour.value = null
        currentNotificationGroupMinute.value = null
        currentNotificationGroupGarbageType.value = GarbageType.REGULAR
    }
}
