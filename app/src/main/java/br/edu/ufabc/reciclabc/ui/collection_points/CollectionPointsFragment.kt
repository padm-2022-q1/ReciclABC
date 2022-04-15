package br.edu.ufabc.reciclabc.ui.collection_points

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.edu.ufabc.reciclabc.databinding.FragmentCollectionPointsBinding

class CollectionPointsFragment : Fragment() {

    private var _binding: FragmentCollectionPointsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val collectionPointsViewModel =
            ViewModelProvider(this).get(CollectionPointsViewModel::class.java)

        _binding = FragmentCollectionPointsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCollectionPoints
        collectionPointsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}