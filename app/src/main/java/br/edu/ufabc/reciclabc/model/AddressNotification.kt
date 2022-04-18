package br.edu.ufabc.reciclabc.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressNotification(
    val id: Long,
    val address: String,
    val notifications: List<Notification>,
) : Parcelable {

    val regularGarbage: List<Notification>
        get() = notifications.filter { n -> n.category == GarbageType.REGULAR }

    val recyclableGarbage: List<Notification>
        get() = notifications.filter { n -> n.category == GarbageType.RECYCLABLE }
}
