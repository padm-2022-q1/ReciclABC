package br.edu.ufabc.reciclabc.model.repository

import br.edu.ufabc.reciclabc.model.Address
import br.edu.ufabc.reciclabc.model.Notification
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class AddressNotificationRepository {
    private lateinit var addresses: MutableList<Address>

    /**
     * Read Json File.
     */
    fun loadData(inputStream: InputStream) {
        val parsedAddress: List<Address> = Klaxon().parseArray(inputStream) ?: emptyList()
        addresses =
            parsedAddress.sortedBy { notification -> notification.name }.toMutableList()
    }

    fun getAll() = if (this::addresses.isInitialized) addresses
    else throw UninitializedPropertyAccessException("Load data first")

    suspend fun createAddress(
        address: Address,
    ): Long  = withContext(Dispatchers.IO) {
        val newId = addresses.size.toLong() + 1
        addresses.add(Address(newId, address.name, address.notifications))
        newId
    }

    suspend fun updateAddress(
        address: Address,
    ) {
        // TODO: Update address
        /*
         * Create notifications which id is 0
         * Remove the missing notifications
         */
    }

    suspend fun getAddressById(id: Long) : Address = withContext(Dispatchers.IO) {
        addresses.find { it.id == id } ?: throw Exception("Not found")
    }

    suspend fun getNotificationById(id: Long) : Notification = withContext(Dispatchers.IO) {
        addresses.flatMap { it.notifications }.find { it.id == id } ?: throw Exception("Not found")
    }
}
