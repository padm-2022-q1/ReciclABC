package br.edu.ufabc.reciclabc.model.room.daos

import androidx.room.*
import br.edu.ufabc.reciclabc.model.room.entities.AddressWithNotifications
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationGroupEntity
import br.edu.ufabc.reciclabc.model.room.entities.Notifications

@Dao
interface NotificationGroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(notificationGroupEntity: NotificationGroupEntity): Long

    @Delete
    fun delete(notificationGroupEntity: NotificationGroupEntity)

    @Update
    fun update(notificationGroupEntity: NotificationGroupEntity)

    @Transaction
    fun upsert(notificationGroupEntity: NotificationGroupEntity): Long {
        var id = notificationGroupEntity.id
        if (id > 0)
            update(notificationGroupEntity)
        else
            id = insert(notificationGroupEntity)
        return id
    }

    @Transaction
    @Query("SELECT * FROM NotificationGroupEntity")
    fun getAll(): List<Notifications>

    @Transaction
    @Query("SELECT * FROM NotificationGroupEntity WHERE id=:id")
    fun getById(id: Long): Notifications

    @Query("UPDATE NotificationGroupEntity SET isActive = :active WHERE id = :id")
    fun toggleActive(id: Long, active: Boolean)
}
