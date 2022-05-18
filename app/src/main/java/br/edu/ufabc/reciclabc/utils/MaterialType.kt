package br.edu.ufabc.reciclabc.utils

import android.content.Context
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.model.MaterialType

fun materialTypeToString(context: Context, material: MaterialType) =
    when(material) {
        MaterialType.BATTERIES -> context.resources.getString(R.string.material_batteries)
        MaterialType.CONSTRUCTION_WASTE -> context.resources.getString(R.string.material_construction_waste)
        MaterialType.COOKING_OIL -> context.resources.getString(R.string.material_kitchen_oil)
        MaterialType.ELECTRONICS -> context.resources.getString(R.string.material_electronics)
        MaterialType.GLASS -> context.resources.getString(R.string.material_glass)
        MaterialType.METAL -> context.resources.getString(R.string.material_metal)
        MaterialType.PAPER -> context.resources.getString(R.string.material_paper)
        MaterialType.PLASTIC -> context.resources.getString(R.string.material_plastic)
    }