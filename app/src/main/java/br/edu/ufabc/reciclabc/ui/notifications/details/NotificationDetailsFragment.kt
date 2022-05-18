package br.edu.ufabc.reciclabc.ui.notifications.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentCreateNotificationBinding
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Weekday
import br.edu.ufabc.reciclabc.ui.shared.Status
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NotificationDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCreateNotificationBinding
    private val viewModel: AddressDetailsViewModel by navGraphViewModels(R.id.navigation_notifications)
    private val args: NotificationDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.notificationGroupId != 0L) {
            if (viewModel.currentNotificationGroupId.value == null) {
                viewModel.loadNotificationGroup(args.notificationGroupId).observe(viewLifecycleOwner) {
                    if (it.status is Status.Error) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.notifications_error_load_notification),
                            Snackbar.LENGTH_LONG
                        ).show()
                        findNavController().navigateUp()
                    }
                }
            }
        } else {
            viewModel.currentNotificationGroupId.value = null
        }
        setupFields()
        setupHandlers()
    }

    private fun setupHandlers() {
        binding.createNotificationTime.editText?.setOnClickListener {
            val c = Calendar.getInstance()

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(viewModel.currentNotificationGroupHour.value ?: c.get(Calendar.HOUR))
                .setMinute(viewModel.currentNotificationGroupMinute.value ?: c.get(Calendar.MINUTE))
                .setTitleText(getString(R.string.notifications_pick_hour))
                .build()

            picker.addOnPositiveButtonClickListener {
                viewModel.currentNotificationGroupHour.value = picker.hour
                viewModel.currentNotificationGroupMinute.value = picker.minute
                fillTimeField()
            }

            picker.show(this.parentFragmentManager, "picker")
        }

        binding.createNotificationRecyclableCategoryRadioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                binding.createNotificationRegularGarbage.id -> viewModel.currentNotificationGroupGarbageType.value =
                    GarbageType.REGULAR
                binding.createNotificationRecyclableGarbage.id -> viewModel.currentNotificationGroupGarbageType.value =
                    GarbageType.RECYCLABLE
            }
        }

        bindWeekdaysEvents()

        binding.createNotificationButton.setOnClickListener { handleSaveClick() }
    }

    private fun setupFields() {
        if (viewModel.currentNotificationGroupId.value != null) {
            binding.createNotificationButton.text =
                getString(R.string.edit_notification_button_text)
        }

        viewModel.currentNotificationGroupWeekdays.observe(viewLifecycleOwner) {
            setCheckedWeekdays(it.toList())
        }

        viewModel.currentNotificationGroupGarbageType.observe(viewLifecycleOwner) {
            when (it) {
                GarbageType.REGULAR -> binding.createNotificationRegularGarbage.isChecked = true
                GarbageType.RECYCLABLE -> binding.createNotificationRecyclableGarbage.isChecked =
                    true
                null -> {}
            }
        }

        viewModel.currentNotificationGroupHour.observe(viewLifecycleOwner) { fillTimeField() }
        viewModel.currentNotificationGroupMinute.observe(viewLifecycleOwner) { fillTimeField() }
    }

    private fun fillTimeField() {
        if (viewModel.currentNotificationGroupHour.value == null || viewModel.currentNotificationGroupMinute.value == null) {
            return
        }
        binding.createNotificationTime.editText?.setText(
            getString(
                R.string.create_notification_time_format,
                viewModel.currentNotificationGroupHour.value,
                viewModel.currentNotificationGroupMinute.value,
            )
        )
    }

    private fun bindWeekdaysEvents() {
        binding.createNotificationWeekdaysSunday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.SUNDAY)
        }
        binding.createNotificationWeekdaysMonday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.MONDAY)
        }
        binding.createNotificationWeekdaysTuesday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.TUESDAY)
        }
        binding.createNotificationWeekdaysWednesday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.WEDNESDAY)
        }
        binding.createNotificationWeekdaysThursday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.THURSDAY)
        }
        binding.createNotificationWeekdaysFriday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.FRIDAY)
        }
        binding.createNotificationWeekdaysSaturday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(isChecked, Weekday.SATURDAY)
        }
    }

    private fun setCheckedWeekdays(weekdays: List<Weekday>) {
        binding.createNotificationWeekdaysSunday.isChecked = weekdays.contains(Weekday.SUNDAY)
        binding.createNotificationWeekdaysMonday.isChecked = weekdays.contains(Weekday.MONDAY)
        binding.createNotificationWeekdaysTuesday.isChecked = weekdays.contains(Weekday.TUESDAY)
        binding.createNotificationWeekdaysWednesday.isChecked = weekdays.contains(Weekday.WEDNESDAY)
        binding.createNotificationWeekdaysThursday.isChecked = weekdays.contains(Weekday.THURSDAY)
        binding.createNotificationWeekdaysFriday.isChecked = weekdays.contains(Weekday.FRIDAY)
        binding.createNotificationWeekdaysSaturday.isChecked = weekdays.contains(Weekday.SATURDAY)
    }

    private fun weekdayChanged(isChecked: Boolean, weekday: Weekday) {
        if (isChecked) viewModel.currentNotificationGroupWeekdays.value?.add(weekday)
        else viewModel.currentNotificationGroupWeekdays.value?.remove(weekday)
        // notify observers
        viewModel.currentNotificationGroupWeekdays.value = viewModel.currentNotificationGroupWeekdays.value
    }

    private fun validate(): Boolean {
        if (viewModel.currentNotificationGroupHour.value == null
            || viewModel.currentNotificationGroupMinute.value == null
            || viewModel.currentNotificationGroupWeekdays.value?.isEmpty() == true
        ) {
            Snackbar.make(binding.root, getString(R.string.notifications_missing_fields), Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun handleSaveClick() {
        validate() || return
        viewModel.saveNotificationGroup().observe(viewLifecycleOwner) {
            if (it.status == Status.Success) {
                findNavController().navigateUp()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.notifications_error_save_notification),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}
