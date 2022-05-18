package br.edu.ufabc.reciclabc.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentNotificationsBinding
import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.ui.shared.Status
import com.google.android.material.snackbar.Snackbar

class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModel.allAddressNotification().observe(viewLifecycleOwner) { response ->
            when (response.status) {
                is Status.Error -> {
                    Log.e("VIEW", "Failed to fetch metadata list", response.status.e)
                    Snackbar.make(
                        binding.root, "Failed to list notifications",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is Status.Success -> {
                    setupNotifications(response.result!!)
                }
            }
        }
    }

    private fun setupNotifications(notifications : List<Address>) {
        binding.rvNotifications.apply {
            val onEditAddressNotificationClicked = { address: Address ->
                val action = NotificationsFragmentDirections.notificationGroupDetailsAction(
                    address.id
                )
                this.findNavController().navigate(action)
            }

            val onDeleteAddressNotificationClicked = { addressNotificationId: Long ->
                viewModel.deleteAddressNotification(addressNotificationId).observe(viewLifecycleOwner) { response ->
                    when (response.status) {
                        is Status.Error -> {
                            Log.e("VIEW", "Failed to delete address notifications", response.status.e)
                            Snackbar.make(
                                binding.root, "Failed to delete address notifications",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is Status.Success -> {
                            fetchNotifications()
                        }
                    }
                }
            }

            val onToggleNotification = { notificationId: Long, isActive: Boolean ->
                viewModel.toggleNotification(notificationId, isActive).observe(viewLifecycleOwner) { response ->
                    when (response.status) {
                        is Status.Error -> {
                            Log.e("VIEW", "Failed to toggle notification", response.status.e)
                            Snackbar.make(
                                binding.root, "Failed to toggle notification",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is Status.Success -> {
                            fetchNotifications()
                        }
                    }
                }
            }

            adapter = AddressNotificationAdapter(
                notifications,
                onEditAddressNotificationClicked,
                onDeleteAddressNotificationClicked,
                onToggleNotification,
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_notification_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.add_address_notification -> {
                val action = NotificationsFragmentDirections.notificationGroupDetailsAction()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}
