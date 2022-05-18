package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentCollectionPointsBinding
import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.MaterialType
import br.edu.ufabc.reciclabc.utils.materialTypeToString
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class CollectionPointsFragment : Fragment() {
    private lateinit var binding: FragmentCollectionPointsBinding
    private val viewModel: CollectionPointsViewModel by viewModels()
    private lateinit var mapManager: MapManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapManager = MapManager(
            requireContext(),
            viewModel,
            requireActivity().activityResultRegistry,
            LocationServices.getFusedLocationProviderClient(requireContext())
        ) { message ->
            Snackbar.make(
                binding.root,
                message,
                Snackbar.LENGTH_LONG
            ).show()
        }
        lifecycle.addObserver(mapManager)

        // https://www.gsrikar.com/2018/12/read-metadata-from-andriod-manifest.html
        requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY")?.let {
                Places.initialize(requireContext(), it)
            }
    }

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
        mapFragment?.getMapAsync(handleMapReady)

        binding.collectionPointsDetailsCard.apply {
            // to stop both click and drag events from propagating to the map underneath
            root.isClickable = true

            cpDetailsMapButton.setOnClickListener { handleCardMapButtonClick() }
            cpDetailsNotificationButton.setOnClickListener { handleCardNotificationButtonClick() }
        }

        viewModel.collectionPoints.observe(viewLifecycleOwner) { collectionPoints ->
            collectionPoints.forEach { mapManager.addMarker(it) }

            if (collectionPoints.isEmpty() && !viewModel.materialFilter.value.isNullOrEmpty()) {
                Snackbar.make(
                    view,
                    "Não há pontos de coleta que aceitam os materiais selecionados",
                    Snackbar.LENGTH_LONG
                ).setAction("Editar filtro") { handleFilterButtonClick() }.show()
            }
        }
        viewModel.selectedMarker.observe(viewLifecycleOwner) { handleSelectedMarkerChange(it) }
        binding.collectionPointsSearchButton.setOnClickListener { mapManager.openPlaceSearch() }
        binding.collectionPointsFilterButton.setOnClickListener { handleFilterButtonClick() }
        binding.collectionPointsMyLocationButton.setOnClickListener { mapManager.goToCurrentLocation() }
    }

    private val handleMapReady = OnMapReadyCallback { googleMap ->
        mapManager.mapReady(googleMap)
        viewModel.filterCollectionPoints()
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
            recyclerviewMaterials.adapter = MaterialTypeChipsAdapter(collectionPoint.materials.toList())
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

    private fun handleCardNotificationButtonClick() {
        viewModel.selectedMarker.value?.let { id ->
            viewModel.getCollectionPointById(id)?.let { cp ->
                CollectionPointsFragmentDirections.createFilledAddressNotificationAction(
                    address = cp.address
                ).let {
                    findNavController().navigate(it)
                }
            }
        }
    }

    private fun handleFilterButtonClick() {
        context?.let { ctx ->
            val materials =
                MaterialType.values().associateBy { materialTypeToString(ctx, it) }
            val materialsKeys = materials.keys.toTypedArray()
            val selectedFilters = materials.values.map { viewModel.materialFilter.value?.contains(it) ?: false }.toBooleanArray()

            MaterialAlertDialogBuilder(ctx)
                .setTitle(getString(R.string.filter_by_material))
                .setNeutralButton(getString(R.string.clear_all)) { _, _ ->
                    Log.d("CollectionPoints", "Filter: clear all")
                    viewModel.clearMaterialFilter()
                    removeOldCollectionPointsMarkers()
                    viewModel.filterCollectionPoints()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    Log.d("CollectionPoints", "Filter: cancel")
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    Log.d("CollectionPoints", "Filter: ok")
                    removeOldCollectionPointsMarkers()
                    viewModel.filterCollectionPoints()
                }
                .setMultiChoiceItems(materialsKeys, selectedFilters) { _, which, checked ->
                    Log.d("CollectionPoints", "Filter: change $which $checked")
                    materials[materialsKeys[which]]?.let {
                        if (checked) {
                            viewModel.addMaterialFilterOption(it)
                        } else {
                            viewModel.removeMaterialFilterOption(it)
                        }
                    }
                }
                .show()
        }
    }

    private fun removeOldCollectionPointsMarkers() {
        val collectionPointsIds = viewModel.collectionPoints.value?.map { it.id } ?: listOf()
        collectionPointsIds.forEach { mapManager.removeMarker(it) }
    }
}
