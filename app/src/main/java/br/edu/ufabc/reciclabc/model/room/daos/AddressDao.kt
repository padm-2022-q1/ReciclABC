package br.edu.ufabc.reciclabc.model.room.daos

import androidx.room.*
import br.edu.ufabc.reciclabc.model.room.entities.AddressEntity
import br.edu.ufabc.reciclabc.model.room.entities.AddressWithNotifications

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(addressEntity: AddressEntity): Long

    @Delete
    fun delete(addressEntity: AddressEntity)

    @Update
    fun update(addressEntity: AddressEntity)

    @Transaction
    @Query("SELECT * FROM addressEntity")
    fun getAll(): List<AddressWithNotifications>

    @Transaction
    @Query("SELECT * FROM addressEntity WHERE id=:id")
    fun getById(id: Long): AddressWithNotifications
}