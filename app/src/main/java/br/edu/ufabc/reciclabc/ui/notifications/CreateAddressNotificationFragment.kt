package br.edu.ufabc.reciclabc.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.edu.ufabc.reciclabc.databinding.FragmentCreateAddressNotificationBinding
import br.edu.ufabc.reciclabc.model.Notification

class CreateAddressNotificationFragment : Fragment() {
    private lateinit var binding: FragmentCreateAddressNotificationBinding
    private val viewModel: CreateNotificationViewModel by activityViewModels()
    private val args: CreateAddressNotificationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAddressNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        args.addressNotification?.apply {
            // TODO: Check lifecycle set address as args's address
            viewModel.setAddress(this.address)
        }

        binding.createAddressNotificationAddress.editText?.doOnTextChanged { inputText, _, _, _ ->
            viewModel.setAddress(inputText.toString())
        }

        binding.createAddressNotificationNotificationList.apply {
            val editCallback = { notification: Notification ->
                val action = CreateAddressNotificationFragmentDirections.createNotificationAction(
                    notification
                )
                this.findNavController().navigate(action)
            }

            val deleteCallback = { notificationId: Long ->
                viewModel.deleteNotification(notificationId)
            }

            adapter = CreateNotificationAdapter(
                args.addressNotification?.notifications ?: emptyList(),
                editCallback,
                deleteCallback,
            )
        }

        binding.createAddressNotificationAddNotificationButton.setOnClickListener {
            val action = CreateAddressNotificationFragmentDirections.createNotificationAction()
            it.findNavController().navigate(action)
        }

        viewModel.address.observe(this) {
            if (it.isEmpty()) {
                binding.createAddressNotificationAddress.error = "Endereço obrigatório"
            } else {
                binding.createAddressNotificationAddress.error = null
            }
        }
    }
}