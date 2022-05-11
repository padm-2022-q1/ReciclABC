package br.edu.ufabc.reciclabc.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.edu.ufabc.reciclabc.App

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    val addressNotificationRepository = (application as App).addressNotificationRepository

    fun allAddressNotification() = addressNotificationRepository.getAll()
}
