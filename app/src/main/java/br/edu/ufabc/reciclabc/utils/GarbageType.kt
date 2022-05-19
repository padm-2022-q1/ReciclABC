package br.edu.ufabc.reciclabc.utils

import android.content.Context
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.model.GarbageType

fun garbageTypeToString(context: Context, garbageType: GarbageType) =
    when(garbageType) {
        GarbageType.REGULAR -> context.resources.getString(R.string.garbage_regular)
        GarbageType.RECYCLABLE -> context.resources.getString(R.string.garbage_recyclabe)
    }
