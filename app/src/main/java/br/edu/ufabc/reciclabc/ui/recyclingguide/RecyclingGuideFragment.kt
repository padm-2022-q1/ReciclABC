package br.edu.ufabc.reciclabc.ui.recyclingguide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.databinding.FragmentRecyclingGuideBinding
import br.edu.ufabc.reciclabc.databinding.RecyclingGuideItemBinding
import br.edu.ufabc.reciclabc.model.RecyclingInformation

class RecyclingGuideFragment : Fragment() {

    private lateinit var binding: FragmentRecyclingGuideBinding
    private val viewModel: RecyclingGuideViewModel by viewModels()

    private inner class RecyclingGuideAdapter(val recycling_info: List<RecyclingInformation>) : RecyclerView.Adapter<RecyclingGuideAdapter.RecyclingGuideHolder>() {

        private inner class RecyclingGuideHolder(itemBinding: RecyclingGuideItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            val title = itemBinding.recyclingGuideItemTitle
            val content = itemBinding.recyclingGuideItemContent
//
//            init {
//                itemBinding.root.setOnClickListener {
//                    viewModel.clickedItemId.value = getItemId(bindingAdapterPosition)
//                }
//            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclingGuideHolder =
            RecyclingGuideHolder(
                RecyclingGuideItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: RecyclingGuideHolder, position: Int) {
            val info = recycling_info[position]
            holder.title.text = info.title
            holder.content.text = info.content
        }

        override fun getItemCount(): Int = recycling_info.size

        override fun getItemId(position: Int): Long = recycling_info[position].id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclingGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.recyclerviewRecyclingGuide.apply {
            adapter = RecyclingGuideAdapter(viewModel.allRecyclingInformation())
        }
    }
}