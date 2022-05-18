package br.edu.ufabc.reciclabc.model.room.daos

import androidx.room.*
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(notificationEntity: NotificationEntity): Long

    @Delete
    fun delete(notificationEntity: NotificationEntity)

    @Update
    fun update(notificationEntity: NotificationEntity)

    @Update
    fun upsert(notificationEntity: NotificationEntity) {
        val id = insert(notificationEntity)
        if (id == -1L) {
            update(notificationEntity)
        }
    }

    @Query("UPDATE NotificationEntity SET isActive = :active WHERE id = :id")
    fun toggleActive(id: Long, active: Boolean)
}
