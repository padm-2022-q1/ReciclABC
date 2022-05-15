package br.edu.ufabc.reciclabc.ui.notifications.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentNotificationGroupDetailsBinding
import br.edu.ufabc.reciclabc.model.Notification
import com.google.android.material.snackbar.Snackbar

class AddressDetailsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationGroupDetailsBinding
    private val args: AddressDetailsFragmentArgs by navArgs()

    /*
     * The viewModel is scoped to the navigation graph.
     */
    private val viewModel: AddressDetailsViewModel by navGraphViewModels(R.id.navigation_notifications)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationGroupDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.notificationGroupId > 0) {
            if (viewModel.currentAddressId.value == null) {
                viewModel.loadAddress(args.notificationGroupId).observe(viewLifecycleOwner) {
                    if (it.status is AddressDetailsViewModel.Status.Error) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.notifications_error_load_address),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
            viewModel.currentAddressId.value = args.notificationGroupId
        } else {
            viewModel.currentAddressId.value = null
        }
        setupFields()
        setupHandlers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backupFields()
    }

    private fun setupFields() {
        viewModel.currentAddressName.observe(viewLifecycleOwner) {
            binding.createAddressNotificationAddress.editText?.setText(it)
            binding.createAddressNotificationAddress.editText?.setSelection(it.length)
        }

        binding.createAddressNotificationAddress.editText?.doOnTextChanged { _, start, before, count ->
            if (count == 0 && start == 0 && before > 0) {
                binding.createAddressNotificationAddress.error = getString(R.string.notifications_required_address)
            } else {
                binding.createAddressNotificationAddress.error = null
            }
        }

        viewModel.currentNotificationList.observe(viewLifecycleOwner) { notifications ->
            binding.createAddressNotificationNotificationList.apply {
                adapter = CreateNotificationAdapter(
                    notifications,
                    { handleEditNotificationClick(it) },
                    { handleDeleteNotificationClick(it) },
                )
            }
        }
    }

    private fun backupFields() {
        viewModel.currentAddressName.value =
            binding.createAddressNotificationAddress.editText?.text.toString()
    }

    private fun setupHandlers() {
        binding.createAddressNotificationAddNotificationButton.setOnClickListener {
            handleAddNotificationClick()
        }
        binding.createAddressNotificationSaveNotificationButton.setOnClickListener { handleSaveAddress() }
    }

    private fun handleAddNotificationClick() {
        val action = AddressDetailsFragmentDirections.createNotificationAction(-1)
        findNavController().navigate(action)
    }

    private fun handleEditNotificationClick(notification: Notification) {
        val action = AddressDetailsFragmentDirections.createNotificationAction(
            notification.id
        )
        this.findNavController().navigate(action)
    }

    private fun handleDeleteNotificationClick(notificationId: Long) {
        viewModel.deleteNotification(notificationId)
    }

    private fun validate(): Boolean {
        if (binding.createAddressNotificationAddress.editText?.text?.length == 0
            || viewModel.currentNotificationList.value?.size == 0
        ) {
            Snackbar.make(binding.root, getString(R.string.notifications_missing_fields), Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun handleSaveAddress() {
        validate() || return
        viewModel.currentAddressName.value =
            binding.createAddressNotificationAddress.editText?.text.toString()
        viewModel.saveAddress().observe(viewLifecycleOwner) {
            when (it.status) {
                is AddressDetailsViewModel.Status.Error -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.notifications_error_save_address),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is AddressDetailsViewModel.Status.Success -> {
                    findNavController().navigateUp()
                }
            }
        }
    }
}
