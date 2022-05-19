package br.edu.ufabc.reciclabc.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.NotificationItemBinding
import br.edu.ufabc.reciclabc.model.NotificationGroup
import br.edu.ufabc.reciclabc.utils.extensions.weekdaysToAbbreviationString

class NotificationAdapter(
    private val notifications: List<NotificationGroup>,
    private val onToggleNotification: ((notificationId: Long, isChecked: Boolean) -> Unit),
    ) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemBinding: NotificationItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val switch = itemBinding.notificationItemSwitch
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
        val notificationGroup = notifications[position]
        val context = holder.itemView.context

        holder.switch.text = context.getString(
            R.string.format_weekdays_time,
            weekdaysToAbbreviationString(
                holder.itemView.context,
                notificationGroup.getWeekDays()
            ),
            notificationGroup.hours,
            notificationGroup.minutes,
            )
        holder.switch.isChecked = notificationGroup.isActive
        holder.switch.setOnCheckedChangeListener { _, isChecked -> onToggleNotification(notificationGroup.id, isChecked) }
    }

    override fun getItemCount(): Int = notifications.size
}
