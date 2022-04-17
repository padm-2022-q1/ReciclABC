package br.edu.ufabc.reciclabc.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import br.edu.ufabc.reciclabc.databinding.FragmentNotificationsBinding
import br.edu.ufabc.reciclabc.model.AddressNotification

class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.rvNotifications.apply {
            val onEditAddressNotificationClicked = { addressNotification: AddressNotification ->
                val action = NotificationsFragmentDirections.createAddressNotificationAction(
                    addressNotification
                )
                this.findNavController().navigate(action)
            }

            val onDeleteAddressNotificationClicked = { addressNotificationId: Long ->
                // TODO: Delete address notification
//                viewModel.delete(addressNotificationId)
            }

            adapter = AddressNotificationAdapter(
                viewModel.allAddressNotification(),
                onEditAddressNotificationClicked,
                onDeleteAddressNotificationClicked,
            )
        }

        binding.floatingActionButton.setOnClickListener {
            val action = NotificationsFragmentDirections.createAddressNotificationAction()
            it.findNavController().navigate(action)
        }
    }
}