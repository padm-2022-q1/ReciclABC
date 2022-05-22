package br.edu.ufabc.reciclabc.ui.notifications.details

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentNotificationGroupDetailsBinding
import br.edu.ufabc.reciclabc.model.NotificationGroup
import br.edu.ufabc.reciclabc.ui.shared.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        if (args.addressId > 0) {
            if (viewModel.currentAddressId.value == null) {
                viewModel.loadAddress(args.addressId).observe(viewLifecycleOwner) {
                    if (it.status is Status.Error) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.notifications_error_load_address),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        else args.address?.let { address -> viewModel.currentAddressName.value = address }

        addBackButtonPressedDispatcher()
        setupFields()
        setupHandlers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backupFields()
    }

    private fun setupFields() {
        viewModel.currentAddressName.observe(viewLifecycleOwner) {
            binding.edittextAddress.editText?.setText(it)
            binding.edittextAddress.editText?.setSelection(it.length)
        }

        binding.edittextAddress.editText?.doOnTextChanged { text, start, before, _ ->
            if ((start > 0 || before > 0) && text?.trim().isNullOrEmpty()) {
                binding.edittextAddress.error = getString(R.string.notifications_required_address)
            } else {
                binding.edittextAddress.error = null
            }
        }

        viewModel.currentNotificationGroupList.observe(viewLifecycleOwner) { notificationGroups ->
            binding.recyclerviewNotifications.apply {
                adapter = CreateNotificationAdapter(
                    notificationGroups,
                    { handleEditNotificationClick(it) },
                    { handleDeleteNotificationClick(it) },
                    { id, isActive -> viewModel.toggleNotification(id, isActive) }
                )
            }
        }
    }

    private fun backupFields() {
        viewModel.currentAddressName.value =
            binding.edittextAddress.editText?.text.toString()
    }

    private fun setupHandlers() {
        binding.buttonAddNotification.setOnClickListener {
            handleAddNotificationClick()
        }
        binding.buttonSaveNotificationGroup.setOnClickListener { handleSaveAddress() }
    }

    private fun handleAddNotificationClick() {
        val action = AddressDetailsFragmentDirections.createNotificationGroupAction()
        viewModel.clearCurrentNotificationGroup()
        findNavController().navigate(action)
    }

    private fun handleEditNotificationClick(notificationGroup: NotificationGroup) {
        val action = AddressDetailsFragmentDirections.createNotificationGroupAction(
            notificationGroup.id
        )
        viewModel.clearCurrentNotificationGroup()
        this.findNavController().navigate(action)
    }

    private fun handleDeleteNotificationClick(notificationGroupId: Long) {
        viewModel.deleteNotificationGroup(notificationGroupId)
    }

    private fun validate(): Boolean {
        if (binding.edittextAddress.editText?.text?.trim()?.isEmpty() != false
            || viewModel.currentNotificationGroupList.value?.size == 0
        ) {
            Snackbar.make(binding.root, getString(R.string.notifications_missing_fields), Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun handleSaveAddress() {
        validate() || return
        viewModel.currentAddressName.value =
            binding.edittextAddress.editText?.text.toString()
        viewModel.saveAddress().observe(viewLifecycleOwner) {
            when (it.status) {
                is Status.Error -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.notifications_error_save_address),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is Status.Success -> {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun addBackButtonPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.addressHasChanged(binding.edittextAddress.editText?.text.toString()))  {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.exit_without_saving_confirmation_text))
                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
                    .setPositiveButton(getString(R.string.yes)) { _, _ -> findNavController().navigateUp() }
                    .show()
            } else {
                findNavController().navigateUp()
            }
        }
    }
}
