package br.edu.ufabc.reciclabc.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.databinding.AddressNotificationBinding
import br.edu.ufabc.reciclabc.model.AddressNotification

class AddressNotificationAdapter(private val addressNotifications: List<AddressNotification>) :
    RecyclerView.Adapter<AddressNotificationAdapter.AddressNotificationViewHolder>() {

    class AddressNotificationViewHolder(itemBinding: AddressNotificationBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val address = itemBinding.tvNotificationItemAddress
        val regularGarbageNotificationLabel = itemBinding.tvNotificationItemRegularOptionsLabel
        val regularGarbageNotifications = itemBinding.rvNotificationItemRegular
        val recyclableGarbageNotificationLabel =
            itemBinding.tvNotificationItemRecyclableOptionsLabel
        val recyclableGarbageNotifications = itemBinding.rvNotificationItemRecyclable

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
        val addressNotification = addressNotifications[position]

        holder.address.text = addressNotification.address

        if (addressNotification.regularGarbage.isNotEmpty()) {
            holder.regularGarbageNotifications.apply {
                adapter = NotificationAdapter(
                    addressNotification.regularGarbage
                )
            }
        } else {
            holder.regularGarbageNotificationLabel.visibility = View.GONE
        }

        if (addressNotification.recyclableGarbage.isNotEmpty()) {
            holder.recyclableGarbageNotifications.apply {
                adapter = NotificationAdapter(
                    addressNotification.recyclableGarbage
                )
            }
        } else {
            holder.recyclableGarbageNotificationLabel.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = addressNotifications.size

    override fun getItemId(position: Int): Long = addressNotifications[position].id
}