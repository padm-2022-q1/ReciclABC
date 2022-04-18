package br.edu.ufabc.reciclabc.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.NotificationItemBinding
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.utils.extensions.weekdaysToAbbreviationString

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemBinding: NotificationItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val switch = itemBinding.notificationItemSwitch

        init {
            switch.setOnCheckedChangeListener { buttonView, isChecked ->
                //TODO Change notification isActive value.
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        val context = holder.itemView.context

        holder.switch.text = context.getString(
            R.string.format_weekdays_time,
            weekdaysToAbbreviationString(
                holder.itemView.context,
                notification.weekdays
            ),
            notification.hours,
            notification.minutes,
            )
        holder.switch.isChecked = notification.isActive
    }

    override fun getItemCount(): Int = notifications.size

    override fun getItemId(position: Int): Long = notifications[position].id

}