package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.databinding.MaterialTypeChipBinding
import br.edu.ufabc.reciclabc.model.MaterialType
import br.edu.ufabc.reciclabc.utils.materialTypeToString

class MaterialTypeChipsAdapter(private val materials: List<MaterialType>) :
    RecyclerView.Adapter<MaterialTypeChipsAdapter.MaterialTypeChipsViewHolder>() {

    class MaterialTypeChipsViewHolder(itemBinding: MaterialTypeChipBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val chip = itemBinding.materialTypeChip
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialTypeChipsViewHolder =
        MaterialTypeChipsViewHolder(
            MaterialTypeChipBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MaterialTypeChipsViewHolder, position: Int) {
        val material = materials[position]
        val context = holder.itemView.context

        holder.chip.text = materialTypeToString(context, material)
    }

    override fun getItemCount(): Int = materials.size
}
