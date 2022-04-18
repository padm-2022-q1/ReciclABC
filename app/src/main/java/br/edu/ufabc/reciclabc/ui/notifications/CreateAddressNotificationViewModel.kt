package br.edu.ufabc.reciclabc.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.edu.ufabc.reciclabc.App

class CreateAddressNotificationViewModel(application: Application) : AndroidViewModel(application) {
    val addressNotificationRepository = (application as App).addressNotificationRepository

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address


    fun setAddress(newAddress: String) {
        _address.value = newAddress
    }

    fun deleteNotification(notificationId: Long) {
        // TODO: delete notification
        // addressNotificationRepository.delete(notificationId)
    }
}
