package br.edu.ufabc.reciclabc.ui.notifications.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import br.edu.ufabc.reciclabc.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import br.edu.ufabc.reciclabc.databinding.FragmentNotificationGroupDetailsBinding
import br.edu.ufabc.reciclabc.model.Notification

class AddressDetailsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationGroupDetailsBinding
    private val args: AddressDetailsFragmentArgs by navArgs()

    /*
     * The viewModel is scoped to the navigation graph.
     */
    private val viewModel: AddressDetailsViewModel by navGraphViewModels(R.id.navigation_notifications)

    override fun onStart() {
        super.onStart()

        if (args.notificationGroupId > 0) {
            viewModel.currentAddressId = args.notificationGroupId
        } else {
            viewModel.currentAddressId = null
        }
    }

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
        setupFields()
        setupHandlers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backupFields()
    }

    private fun setupFields() {
        binding.createAddressNotificationAddress.editText?.setText(viewModel.currentAddressName)
        binding.createAddressNotificationAddress.editText?.setSelection(viewModel.currentAddressName.length)
        binding.createAddressNotificationAddress.editText?.doOnTextChanged { _, _, _, count ->
            if (count == 0) {
                // TODO: extract to resources
                binding.createAddressNotificationAddress.error = "Endereço obrigatório"
            } else {
                binding.createAddressNotificationAddress.error = null
            }
        }

        binding.createAddressNotificationNotificationList.apply {
            adapter = CreateNotificationAdapter(
                viewModel.currentNotificationList,
                { handleEditNotificationClick(it) },
                { handleDeleteNotificationClick(it) },
            )
        }
    }

    private fun backupFields() {
        viewModel.currentAddressName =
            binding.createAddressNotificationAddress.editText?.text.toString()
    }

    private fun setupHandlers() {
        binding.createAddressNotificationAddNotificationButton.setOnClickListener {
            handleAddNotificationClick()
        }
        binding.createAddressNotificationSaveNotificationButton.setOnClickListener {
            viewModel.currentAddressName = binding.createAddressNotificationAddress.editText?.text.toString()
            viewModel.saveAddress()
        }
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
}
