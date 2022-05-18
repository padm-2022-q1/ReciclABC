package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.edu.ufabc.reciclabc.model.Address

@Entity
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val address: String,
) {
    companion object {
        fun fromAddressNotification(address: Address) = AddressEntity(
            address.id,
            address.name,
        )
    }
}
