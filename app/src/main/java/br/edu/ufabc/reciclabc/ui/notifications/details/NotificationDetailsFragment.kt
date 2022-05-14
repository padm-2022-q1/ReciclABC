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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NotificationDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCreateNotificationBinding
    private val viewModel: AddressDetailsViewModel by navGraphViewModels(R.id.navigation_notifications)
    private val args: NotificationDetailsFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        if (args.notificationId > 0) {
            // TODO: load notification
            viewModel.loadNotification(args.notificationId)
            /*
             * Esbarrei num problema agora. Atualizar a tela com os dados depois
             * que carregar. Vamos ter que colocar tudo em fucking live data, ou
             * passar a notificação interira como parâmetro, o que eu acho zuado
             * também, mas é o que tava e funcionava. android podia ter rerender
             * saudades react e seus hooks
             * outra ideia agora: carregar na tela anterior, antes da navegacao
             */
        } else {
            viewModel.currentNotificationId = null
        }
    }

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
        setupFields()
        setupHandlers()
    }

    private fun setupHandlers() {
        binding.createNotificationTime.editText?.setOnClickListener {
            val c = Calendar.getInstance()

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(viewModel.currentNotificationHour ?: c.get(Calendar.HOUR))
                .setMinute(viewModel.currentNotificationMinute ?: c.get(Calendar.MINUTE))
                .setTitleText("Select notification time")
                .build()

            picker.addOnPositiveButtonClickListener {
                viewModel.currentNotificationHour = picker.hour
                viewModel.currentNotificationMinute = picker.minute
                fillTimeField()
            }

            picker.show(this.parentFragmentManager, "picker")
        }

        binding.createNotificationRecyclableCategoryRadioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                binding.createNotificationRegularGarbage.id -> viewModel.currentNotificationGarbageType =
                    GarbageType.REGULAR
                binding.createNotificationRecyclableGarbage.id -> viewModel.currentNotificationGarbageType =
                    GarbageType.RECYCLABLE
            }
        }

        bindWeekdaysEvents()

        binding.createNotificationButton.setOnClickListener { handleSaveClick() }
    }

    private fun setupFields() {
        setCheckedWeekdays(viewModel.currentNotificationWeekdays.toList())

        if (viewModel.currentNotificationId != null) {
            binding.createNotificationButton.text =
                getString(R.string.edit_notification_button_text)
        }

        if (viewModel.currentNotificationGarbageType == GarbageType.REGULAR) {
            binding.createNotificationRegularGarbage.isChecked = true
        } else {
            binding.createNotificationRecyclableGarbage.isChecked = true
        }

        fillTimeField()
    }

    private fun fillTimeField() {
        if (viewModel.currentNotificationHour == null || viewModel.currentNotificationMinute == null) {
            return
        }
        binding.createNotificationTime.editText?.setText(
            getString(
                R.string.create_notification_time_format,
                viewModel.currentNotificationHour,
                viewModel.currentNotificationMinute,
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

    private fun weekdayChanged(isChecked: Boolean, weekday: Weekday) {
        if (isChecked) viewModel.currentNotificationWeekdays.add(weekday)
        else if (isChecked) viewModel.currentNotificationWeekdays.remove(weekday)
    }

    private fun validate(): Boolean {
        if (viewModel.currentNotificationHour == null
            || viewModel.currentNotificationMinute == null
            || viewModel.currentNotificationWeekdays.isEmpty()
        ) {
            // TODO: better error message
            Snackbar.make(binding.root, "INCOMPLETO", Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun handleSaveClick() {
        validate() || return
        viewModel.saveNotification().observe(viewLifecycleOwner) {
            if (it.status == AddressDetailsViewModel.Status.Success) {
                findNavController().navigateUp()
            } else {
                // TODO: notify user
            }
        }
    }
}
