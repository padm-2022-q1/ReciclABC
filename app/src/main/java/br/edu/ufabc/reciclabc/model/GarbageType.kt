package br.edu.ufabc.reciclabc.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GarbageType : Parcelable { REGULAR, RECYCLABLE }
