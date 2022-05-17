package br.edu.ufabc.reciclabc.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import br.edu.ufabc.reciclabc.App
import br.edu.ufabc.reciclabc.ui.shared.Result
import br.edu.ufabc.reciclabc.ui.shared.Status
import java.util.*

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val addressNotificationRepository = (application as App).addressNotificationRepository

    fun allAddressNotification() = liveData {
        try {
            emit(Result(addressNotificationRepository.getAll(), Status.Success))
        } catch (e: Exception) {
            emit(Result(emptyList(), Status.Error(Exception("Failed to load address notifications", e))))
        }
    }

    fun deleteAddressNotification(id: Long) = liveData {
        try {
            emit(Result(addressNotificationRepository.delete(id), Status.Success))
        } catch (e: Exception) {
            emit(Result(Unit, Status.Error(Exception("Failed to delete address notification", e))))
        }
    }

    fun toggleNotification(id: Long, isActive: Boolean) = liveData {
        try {
            emit(Result(addressNotificationRepository.toggleNotification(id, isActive), Status.Success))
        } catch (e: Exception) {
            emit(Result(Unit, Status.Error(Exception("Failed to toggle notification", e))))
        }
    }
}
