package br.edu.ufabc.reciclabc.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.ufabc.reciclabc.model.room.daos.AddressDao
import br.edu.ufabc.reciclabc.model.room.daos.NotificationDao
import br.edu.ufabc.reciclabc.model.room.daos.NotificationGroupDao
import br.edu.ufabc.reciclabc.model.room.entities.AddressEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationGroupEntity

@Database(entities = [AddressEntity::class, NotificationGroupEntity::class, NotificationEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun AddressDao(): AddressDao
    abstract fun NotificationGroupDao(): NotificationGroupDao
    abstract fun NotificationDao(): NotificationDao
}
