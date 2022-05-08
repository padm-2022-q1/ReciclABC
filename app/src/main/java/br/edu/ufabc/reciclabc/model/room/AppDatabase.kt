package br.edu.ufabc.reciclabc.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.ufabc.reciclabc.model.room.daos.AddressDao
import br.edu.ufabc.reciclabc.model.room.daos.NotificationDao
import br.edu.ufabc.reciclabc.model.room.entities.AddressEntity
import br.edu.ufabc.reciclabc.model.room.entities.NotificationEntity

@Database(entities = [AddressEntity::class, NotificationEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun AddressDao(): AddressDao
    abstract fun NotificationDao(): NotificationDao
}