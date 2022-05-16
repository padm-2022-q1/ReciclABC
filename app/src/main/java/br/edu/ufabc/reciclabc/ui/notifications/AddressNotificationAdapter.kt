package br.edu.ufabc.reciclabc.ui.notifications

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.AddressNotificationBinding
import br.edu.ufabc.reciclabc.model.Address

class AddressNotificationAdapter(
    private val addresses: List<Address>,
    private val onEditAddressNotificationClicked: ((address: Address) -> Unit),
    private val onDeleteAddressNotificationClicked: ((addressNotificationId: Long) -> Unit),
    private val onToggleNotification: ((notificationId: Long, isChecked: Boolean) -> Unit),
    ) :
    RecyclerView.Adapter<AddressNotificationAdapter.AddressNotificationViewHolder>() {

    class AddressNotificationViewHolder(itemBinding: AddressNotificationBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val address = itemBinding.tvNotificationItemAddress
        val regularGarbageNotificationLabel = itemBinding.tvNotificationItemRegularOptionsLabel
        val regularGarbageNotifications = itemBinding.rvNotificationItemRegular
        val recyclableGarbageNotificationLabel =
            itemBinding.tvNotificationItemRecyclableOptionsLabel
        val recyclableGarbageNotifications = itemBinding.rvNotificationItemRecyclable
        val menu = itemBinding.btNotificationItemConfig
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressNotificationViewHolder =
        AddressNotificationViewHolder(
            AddressNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AddressNotificationViewHolder, position: Int) {
        val addressNotification = addresses[position]

        holder.address.text = addressNotification.name

        if (addressNotification.regularGarbage.isNotEmpty()) {
            holder.regularGarbageNotifications.apply {
                adapter = NotificationAdapter(
                    addressNotification.regularGarbage,
                    onToggleNotification,
                )
            }
        } else {
            holder.regularGarbageNotificationLabel.visibility = View.GONE
        }

        if (addressNotification.recyclableGarbage.isNotEmpty()) {
            holder.recyclableGarbageNotifications.apply {
                adapter = NotificationAdapter(
                    addressNotification.recyclableGarbage,
                    onToggleNotification,
                )
            }
        } else {
            holder.recyclableGarbageNotificationLabel.visibility = View.GONE
        }

        holder.menu.setOnClickListener {
            val popup = PopupMenu(it.context, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.notification_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.notification_menu_edit -> {
                        onEditAddressNotificationClicked(addressNotification)
                        true
                    }
                    R.id.notification_menu_delete -> {
                        onDeleteAddressNotificationClicked(addressNotification.id)
                        true
                    }
                    else -> false
                }
            }
            popup.show()

        }
    }

    override fun getItemCount(): Int = addresses.size
}
