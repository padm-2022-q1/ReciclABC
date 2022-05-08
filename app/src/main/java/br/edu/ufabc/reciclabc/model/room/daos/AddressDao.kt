package br.edu.ufabc.reciclabc.model.room.daos

import androidx.room.*
import br.edu.ufabc.reciclabc.model.room.entities.Address
import br.edu.ufabc.reciclabc.model.room.entities.AddressWithNotifications

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(address: Address): Long

    @Delete
    fun delete(address: Address)

    @Update
    fun update(address: Address)

    @Transaction
    @Query("SELECT * FROM address")
    fun getAll(): List<AddressWithNotifications>

    @Transaction
    @Query("SELECT * FROM address WHERE id=:id")
    fun getById(id: Long): AddressWithNotifications
}