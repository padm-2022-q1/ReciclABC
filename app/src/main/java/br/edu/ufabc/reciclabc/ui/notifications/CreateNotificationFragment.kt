package br.edu.ufabc.reciclabc.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import br.edu.ufabc.reciclabc.databinding.FragmentCreateNotificationBinding
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Weekday
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class CreateNotificationFragment : Fragment() {
    private lateinit var binding: FragmentCreateNotificationBinding
    private val viewModel: CreateNotificationViewModel by viewModels()
    private val args: CreateNotificationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        args.notification?.apply {
            if (viewModel.hour.value == null) {
                viewModel.setHour(hours)
            }

            if (viewModel.minute.value == null) {
                viewModel.setMinute(minutes)
            }

            if (viewModel.garbageType.value == null) {
                viewModel.setGarbageType(category)
            }

            if (viewModel.weekdays.value == null) {
                viewModel.setWeekdays(weekdays)
                setCheckedWeekdays(weekdays)
            }
        }

        binding.createNotificationTime.editText?.setOnClickListener {
            val c = Calendar.getInstance()

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(viewModel.hour.value ?: c.get(Calendar.HOUR))
                .setMinute(viewModel.minute.value ?: c.get(Calendar.MINUTE))
                .setTitleText("Select notification time")
                .build()

            picker.addOnPositiveButtonClickListener {
                viewModel.setHour(picker.hour)
                viewModel.setMinute(picker.minute)
            }

            picker.show(this.parentFragmentManager, "picker")
        }

        binding.createNotificationRecyclableCategoryRadioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                binding.createNotificationRecyclableGarbage.id -> viewModel.setGarbageType(
                    GarbageType.RECYCLABLE
                )
                binding.createNotificationRegularGarbage.id -> viewModel.setGarbageType(GarbageType.REGULAR)
            }
        }

        viewModel.garbageType.observe(viewLifecycleOwner) {
            when (it) {
                GarbageType.RECYCLABLE -> binding.createNotificationRecyclableCategoryRadioGroup.check(
                    binding.createNotificationRecyclableGarbage.id
                )
                GarbageType.REGULAR -> binding.createNotificationRecyclableCategoryRadioGroup.check(
                    binding.createNotificationRegularGarbage.id
                )
                else -> {}
            }
        }

        viewModel.timeString.observe(viewLifecycleOwner) {
            it?.let { binding.createNotificationTime.editText?.setText(it) }
        }

        bindWeekdaysEvents()

        binding.createNotificationButton.setOnClickListener {
            viewModel.createNotification()
        }
    }

    private fun bindWeekdaysEvents() {
        binding.createNotificationWeekdaysSunday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.SUNDAY,
            )
        }
        binding.createNotificationWeekdaysMonday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.MONDAY,
            )
        }
        binding.createNotificationWeekdaysTuesday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.TUESDAY,
            )
        }
        binding.createNotificationWeekdaysWednesday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.WEDNESDAY,
            )
        }
        binding.createNotificationWeekdaysThursday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.THURSDAY,
            )
        }
        binding.createNotificationWeekdaysFriday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.FRIDAY,
            )
        }
        binding.createNotificationWeekdaysSaturday.setOnCheckedChangeListener { _, isChecked ->
            weekdayChanged(
                isChecked,
                Weekday.SATURDAY,
            )
        }
    }

    private fun weekdayChanged(isChecked: Boolean, weekday: Weekday) {
        if (isChecked) viewModel.addWeekday(weekday)
        else viewModel.removeWeekday(weekday)
    }

    private fun setCheckedWeekdays(weekdays: List<Weekday>) {
        weekdays.forEach {
            when (it) {
                Weekday.SUNDAY -> binding.createNotificationWeekdaysSunday.isChecked =
                    true
                Weekday.MONDAY -> binding.createNotificationWeekdaysMonday.isChecked =
                    true
                Weekday.TUESDAY -> binding.createNotificationWeekdaysTuesday.isChecked =
                    true
                Weekday.WEDNESDAY -> binding.createNotificationWeekdaysWednesday.isChecked =
                    true
                Weekday.THURSDAY -> binding.createNotificationWeekdaysThursday.isChecked =
                    true
                Weekday.FRIDAY -> binding.createNotificationWeekdaysFriday.isChecked =
                    true
                Weekday.SATURDAY -> binding.createNotificationWeekdaysSaturday.isChecked =
                    true
            }
        }
    }
}