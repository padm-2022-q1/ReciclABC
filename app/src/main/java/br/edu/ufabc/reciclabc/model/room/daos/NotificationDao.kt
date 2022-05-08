package br.edu.ufabc.reciclabc.model.room.daos

import androidx.room.*
import br.edu.ufabc.reciclabc.model.room.entities.Notification

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(notification: Notification): Long

    @Delete
    fun delete(notification: Notification)

    @Update
    fun update(notification: Notification)
}