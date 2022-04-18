package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentCollectionPointsBinding
import br.edu.ufabc.reciclabc.model.CollectionPoint
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class CollectionPointsFragment : Fragment() {

    private lateinit var binding: FragmentCollectionPointsBinding
    private val viewModel: CollectionPointsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.collection_points_map) as SupportMapFragment?
        mapFragment?.getMapAsync(viewModel.handleMapReady)

        binding.collectionPointsDetailsCard.apply {
            // to stop both click and drag events from propagating to the map underneath
            root.isClickable = true

            cpDetailsMapButton.setOnClickListener { handleCardMapButtonClick() }
        }

        viewModel.selectedMarker.observe(viewLifecycleOwner) { handleSelectedMarkerChange(it) }
        binding.collectionPointsFilterButton.setOnClickListener { handleFilterButtonClick() }
    }

    private fun handleSelectedMarkerChange(collectionPointId: Int?) {
        if (collectionPointId == null) {
            hideDetailsCard()
            return
        }

        viewModel.getCollectionPointById(collectionPointId)?.let { cp -> showDetailsCard(cp) }
            ?: run {
                viewModel.selectedMarker.value = null
                Log.e(
                    "CollectionPoints",
                    "Could not find collection point by id $collectionPointId"
                )
                view?.let { view ->
                    Snackbar.make(
                        view,
                        "Could not load collection point details",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showDetailsCard(collectionPoint: CollectionPoint) {
        binding.collectionPointsDetailsCard.apply {
            cpDetailsTitle.text = collectionPoint.name
            cpDetailsAddress.text = collectionPoint.address
            root.visibility = View.VISIBLE
        }
    }

    private fun hideDetailsCard() {
        binding.collectionPointsDetailsCard.root.visibility = View.GONE
    }

    private fun handleCardMapButtonClick() {
        viewModel.selectedMarker.value?.let {
            viewModel.getCollectionPointById(it)?.let { cp ->
                val mapIntentUri = Uri.parse("geo:${cp.lat},${cp.lng}?q=${Uri.encode(cp.name)}")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                startActivity(mapIntent)
            }
        }
    }

    private fun handleFilterButtonClick() {
        val materials = arrayOf(
            getString(R.string.material_paper),
            getString(R.string.material_plastic),
            getString(R.string.material_metal),
            getString(R.string.material_glass),
            getString(R.string.material_kitchen_oil),
            getString(R.string.material_electronics),
            getString(R.string.material_batteries),
        )
        val selectedFilters = BooleanArray(materials.size) { false }

        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle(getString(R.string.filter_by_material))
                .setNeutralButton(getString(R.string.clear_all)) { _, _ ->
                    Log.d("CollectionPoints", "Filter: clear all")
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    Log.d("CollectionPoints", "Filter: cancel")
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    Log.d("CollectionPoints", "Filter: ok")
                }
                .setMultiChoiceItems(materials, selectedFilters) { _, which, checked ->
                    Log.d("CollectionPoints", "Filter: change $which $checked")
                }
                .show()
        }
    }
}
