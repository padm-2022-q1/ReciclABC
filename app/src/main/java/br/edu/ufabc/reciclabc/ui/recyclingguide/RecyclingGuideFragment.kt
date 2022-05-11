package br.edu.ufabc.reciclabc.ui.recyclingguide

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.reciclabc.databinding.FragmentRecyclingGuideBinding
import br.edu.ufabc.reciclabc.databinding.RecyclingGuideItemBinding
import br.edu.ufabc.reciclabc.model.RecyclingInformation

class RecyclingGuideFragment : Fragment() {

    private lateinit var binding: FragmentRecyclingGuideBinding
    private val viewModel: RecyclingGuideViewModel by viewModels()

    private companion object {
        const val EXPAND_COLLAPSE = "EXPAND_COLLAPSE"
    }

    private inner class RecyclingGuideAdapter(val recycling_info: List<RecyclingInformation>) : RecyclerView.Adapter<RecyclingGuideAdapter.RecyclingGuideHolder>() {

        private val transition = AutoTransition().apply { duration = 100 }

        private inner class RecyclingGuideHolder(itemBinding: RecyclingGuideItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            val title = itemBinding.recyclingGuideItemTitle
            val content = itemBinding.recyclingGuideItemContent
            val expandMore = itemBinding.recyclingGuideItemExpandMore
            val expandLess = itemBinding.recyclingGuideItemExpandLess

            init {
                itemBinding.root.setOnClickListener {
                    TransitionManager.beginDelayedTransition(binding.recyclerviewRecyclingGuide, transition)
                    viewModel.expandedPosition.let { expandedPosition ->
                        // collapse currently expanded items
                        if (expandedPosition != RecyclerView.NO_POSITION) {
                            notifyItemChanged(expandedPosition, EXPAND_COLLAPSE)
                        }

                        // expand this item
                        if (expandedPosition != adapterPosition) {
                            viewModel.expandedPosition = adapterPosition
                            notifyItemChanged(adapterPosition, EXPAND_COLLAPSE)
                        } else {
                            viewModel.expandedPosition = RecyclerView.NO_POSITION
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclingGuideHolder =
            RecyclingGuideHolder(
                RecyclingGuideItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: RecyclingGuideHolder, position: Int, payloads: MutableList<Any>) {
            if (payloads.contains(EXPAND_COLLAPSE)) {
                setExpanded(holder, viewModel.expandedPosition == position)
            } else {
                onBindViewHolder(holder, position)
            }
        }

        override fun onBindViewHolder(holder: RecyclingGuideHolder, position: Int) {
            val info = recycling_info[position]
            holder.apply {
                setExpanded(this, viewModel.expandedPosition == position)
                title.text = info.title
                content.text = info.content
            }
        }

        private fun setExpanded(holder: RecyclingGuideHolder, isExpanded: Boolean) {
            holder.itemView.isActivated = isExpanded
            holder.content.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.expandLess.visibility = if (isExpanded) View.VISIBLE else View.INVISIBLE
            holder.expandMore.visibility = if (isExpanded) View.INVISIBLE else View.VISIBLE
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
            itemAnimator = object : DefaultItemAnimator() {
                // Invalid recycler view moves items which causes flash when expanding or collapsing
                override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
                    return false
                }
            }
        }
    }
}
