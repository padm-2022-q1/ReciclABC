package br.edu.ufabc.reciclabc.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import br.edu.ufabc.reciclabc.databinding.FragmentCreateNotificationBinding
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Weekday
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class CreateNotificationFragment : Fragment() {
    private lateinit var binding: FragmentCreateNotificationBinding
    private val viewModel: CreateNotificationViewModel by activityViewModels()
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
            viewModel.setHour(this.hours)
            viewModel.setMinute(this.minutes)
            viewModel.setGarbageType(this.category)
            viewModel.setWeekdays(this.weekdays)

            with(this.weekdays) {
                when {
                    Weekday.SUNDAY in this -> binding.createNotificationWeekdaysSunday.isChecked =
                        true
                    Weekday.MONDAY in this -> binding.createNotificationWeekdaysMonday.isChecked =
                        true
                    Weekday.TUESDAY in this -> binding.createNotificationWeekdaysTuesday.isChecked =
                        true
                    Weekday.WEDNESDAY in this -> binding.createNotificationWeekdaysWednesday.isChecked =
                        true
                    Weekday.THURSDAY in this -> binding.createNotificationWeekdaysThursday.isChecked =
                        true
                    Weekday.FRIDAY in this -> binding.createNotificationWeekdaysFriday.isChecked =
                        true
                    Weekday.SATURDAY in this -> binding.createNotificationWeekdaysSaturday.isChecked =
                        true
                }
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

        binding.createNotificationRecyclableGarbage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                viewModel.setGarbageType(GarbageType.REGULAR)

        }

        binding.createNotificationRegularGarbage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                viewModel.setGarbageType(GarbageType.RECYCLABLE)
        }

        viewModel.garbageType.observe(this) {
            it?.let { type ->
                when (type) {
                    GarbageType.REGULAR -> binding.createNotificationRegularGarbage.isChecked =
                        false
                    GarbageType.RECYCLABLE -> binding.createNotificationRecyclableGarbage.isChecked =
                        false
                }
            }
        }

        viewModel.timeString.observe(this) {
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
}