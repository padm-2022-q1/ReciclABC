package br.edu.ufabc.reciclabc.model.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val address: String,
)