package br.edu.ufabc.reciclabc.ui.notifications.details

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.CreateNotificationItemBinding
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.NotificationGroup
import br.edu.ufabc.reciclabc.utils.extensions.weekdaysToAbbreviationString

class CreateNotificationAdapter(
    private val notificationGroups: List<NotificationGroup>,
    private val onEditNotificationClicked: ((notificationGroup: NotificationGroup) -> Unit),
    private val onDeleteNotificationClicked: ((notificationGroupId: Long) -> Unit),
    private val onEnabledSwitchChange: ((notificationGroupId: Long, checked: Boolean) -> Unit),
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
        val notificationGroup = notificationGroups[position]

        holder.category.text = when(notificationGroup.category) {
            GarbageType.RECYCLABLE -> holder.itemView.context.getString(R.string.create_notification_item_chip_text_recyclable)
            GarbageType.REGULAR -> holder.itemView.context.getString(R.string.create_notification_item_chip_text_regular)
        }
        when (notificationGroup.category) {
            GarbageType.RECYCLABLE -> holder.category.setChipBackgroundColorResource(R.color.recyclable_notification_label)
            GarbageType.REGULAR -> holder.category.setChipBackgroundColorResource(R.color.regular_notification_label)
        }

        holder.time.text = holder.itemView.context?.getString(
            R.string.create_notification_time_format,
            notificationGroup.hours,
            notificationGroup.minutes,
        )

        holder.switch.isChecked = notificationGroup.isActive

        holder.weekdays.text = weekdaysToAbbreviationString(holder.itemView.context, notificationGroup.getWeekDays())

        holder.switch.setOnCheckedChangeListener { _, checked -> onEnabledSwitchChange(notificationGroup.id, checked)}

        holder.menu.setOnClickListener {
            val popup = PopupMenu(it.context, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.notification_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.notification_menu_edit -> {
                        onEditNotificationClicked(notificationGroup)
                        true
                    }
                    R.id.notification_menu_delete -> {
                        onDeleteNotificationClicked(notificationGroup.id)
                        true
                    }
                    else -> false
                }
            }
            popup.show()

        }
    }

    override fun getItemCount(): Int = notificationGroups.size

    override fun getItemId(position: Int): Long = notificationGroups[position].id
}
