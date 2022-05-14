package br.edu.ufabc.reciclabc.ui.notifications.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.edu.ufabc.reciclabc.App
import br.edu.ufabc.reciclabc.model.Notification

class NotificationDetailsViewModel(application: Application) : AndroidViewModel(application) {
    val addressNotificationRepository = (application as App).addressNotificationRepository

    var currentNotificationGroupId: Long? = null

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    var currentAddress = ""
    var currentNotificationList: List<Notification> = mutableListOf()

    fun setAddress(newAddress: String) {
        _address.value = newAddress
    }

    fun deleteNotification(notificationId: Long) {
        // TODO: delete notification
        // addressNotificationRepository.delete(notificationId)
    }
}
