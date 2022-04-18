package br.edu.ufabc.reciclabc.ui.notifications

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.edu.ufabc.reciclabc.App
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Weekday
import br.edu.ufabc.reciclabc.utils.extensions.combine

class CreateNotificationViewModel(application: Application) : AndroidViewModel(application) {
    val addressNotificationRepository = (application as App).addressNotificationRepository

    private val _weekdays = MutableLiveData<MutableSet<Weekday>>()
    val weekdays: LiveData<MutableSet<Weekday>> = _weekdays

    private val _hour = MutableLiveData<Int>()
    val hour: LiveData<Int> = _hour

    private val _minute = MutableLiveData<Int>()
    val minute: LiveData<Int> = _minute

    private val _garbageType = MutableLiveData<GarbageType>()
    val garbageType: LiveData<GarbageType> = _garbageType

    private val _timeString =
        _hour.combine(_minute) { hour, minute -> if (hour != null && minute != null) "$hour:$minute" else "" }
    val timeString: LiveData<String> = _timeString

    fun setWeekdays(weekdays: List<Weekday>) {
        _weekdays.value = weekdays.toMutableSet()
    }

    fun addWeekday(weekday: Weekday) {
        _weekdays.value?.add(weekday)
    }

    fun removeWeekday(weekday: Weekday) {
        _weekdays.value?.remove(weekday)
    }

    fun setHour(hour: Int) {
        _hour.value = hour
    }

    fun setMinute(minute: Int) {
        _minute.value = minute
    }

    fun setGarbageType(type: GarbageType) {
        _garbageType.value = type
    }

    fun createNotification() {
        // TODO: Verify errors
        Log.d("APP", "weekdays = ${_weekdays.value}")
        Log.d("APP", "hour = ${_hour.value}")
        Log.d("APP", "minute = ${_minute.value}")
        Log.d("APP", "garbageType = ${_garbageType.value}")

//        addressNotificationRepository.create()
    }

    fun deleteNotification(notificationId: Long) {
        // TODO: delete notification
        // addressNotificationRepository.delete(notificationId)
    }
}
