package br.edu.ufabc.reciclabc.ui.notifications.createaddressnotification

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.CreateNotificationItemBinding
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Notification
import br.edu.ufabc.reciclabc.utils.extensions.weekdaysToAbbreviationString

class CreateNotificationAdapter(
    private val notifications: List<Notification>,
    private val onEditNotificationClicked: ((notification: Notification) -> Unit),
    private val onDeleteNotificationClicked: ((notificationId: Long) -> Unit),
) :
    RecyclerView.Adapter<CreateNotificationAdapter.CreatedNotificationViewHolder>() {

    class CreatedNotificationViewHolder(itemBinding: CreateNotificationItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val category = itemBinding.createNotificationCategory
        val time = itemBinding.createNotificationTime
        val switch = itemBinding.createNotificationSwitch
        val menu = itemBinding.createNotificationMenu
        val weekdays = itemBinding.createNotificationWeekdays
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreatedNotificationViewHolder =
        CreatedNotificationViewHolder(
            CreateNotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CreatedNotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.category.text = when(notification.category) {
            GarbageType.RECYCLABLE -> holder.itemView.context.getString(R.string.create_notification_item_chip_text_recyclable)
            GarbageType.REGULAR -> holder.itemView.context.getString(R.string.create_notification_item_chip_text_regular)
        }
        when (notification.category) {
            GarbageType.RECYCLABLE -> holder.category.setChipBackgroundColorResource(R.color.teal_200)
            GarbageType.REGULAR -> holder.category.setChipBackgroundColorResource(R.color.purple_200)
        }

        holder.time.text = "${notification.hours}:${notification.minutes}"

        holder.switch.isChecked = notification.isActive

        holder.weekdays.text = weekdaysToAbbreviationString(holder.itemView.context, notification.weekdays)

        holder.menu.setOnClickListener {
            val popup = PopupMenu(it.context, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.notification_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.notification_menu_edit -> {
                        onEditNotificationClicked(notification)
                        true
                    }
                    R.id.notification_menu_delete -> {
                        onDeleteNotificationClicked(notification.id)
                        true
                    }
                    else -> false
                }
            }
            popup.show()

        }
    }

    override fun getItemCount(): Int = notifications.size

    override fun getItemId(position: Int): Long = notifications[position].id
}
