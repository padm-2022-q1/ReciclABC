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

    //    companion object {
//        val INPUT_ADDRESS = "INPUT_USERNAME" to R.string.address
//        val INPUT_TIME = "INPUT_PASSWORD" to R.string.time
//        val INPUT_WEEKDAYS = "INPUT_WEEKDAYS" to R.string.weekdays
//        val INPUT_GARBAGE_TYPE = "INPUT_GARBAGE_TYPE" to R.string.types_of_recycling
//    }
    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private val _weekdays = MutableLiveData<MutableSet<Weekday>>(null)
    val weekdays: LiveData<MutableSet<Weekday>> = _weekdays

    private val _hour = MutableLiveData<Int?>(null)
    val hour: LiveData<Int?> = _hour

    private val _minute = MutableLiveData<Int?>(null)
    val minute: LiveData<Int?> = _minute

    private val _garbageType = MutableLiveData<GarbageType?>(null)
    val garbageType: LiveData<GarbageType?> = _garbageType

    private val _timeString =
        _hour.combine(_minute) { hour, minute -> if (hour != null && minute != null) "$hour:$minute" else "" }
    val timeString: LiveData<String> = _timeString

    fun setAddress(newAddress: String) {
        _address.value = newAddress
    }

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
        Log.d("APP", "address = ${_address.value}")
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
