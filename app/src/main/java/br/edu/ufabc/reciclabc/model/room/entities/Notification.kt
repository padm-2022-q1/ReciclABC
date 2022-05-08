package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import br.edu.ufabc.reciclabc.model.GarbageType
import br.edu.ufabc.reciclabc.model.Weekday

@Entity(foreignKeys = [ForeignKey(entity = Address::class,
    parentColumns = ["id"],
    childColumns = ["addressId"],
    onDelete = ForeignKey.CASCADE)])
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val addressId: Long,
    val category: GarbageType,
    val weekdays: List<Weekday>,
    val hours: Int,
    val minutes: Int,
    val isActive: Boolean,
)