package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.edu.ufabc.reciclabc.R
import br.edu.ufabc.reciclabc.databinding.FragmentCollectionPointsBinding
import br.edu.ufabc.reciclabc.model.CollectionPoint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class CollectionPointsFragment : Fragment() {

    private lateinit var binding: FragmentCollectionPointsBinding
    private val viewModel: CollectionPointsViewModel by viewModels()

    private var requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            requestPermissionsCallback(permissions)
        }
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
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
        mapFragment?.getMapAsync(viewModel.handleMapReady)

        binding.collectionPointsDetailsCard.apply {
            // to stop both click and drag events from propagating to the map underneath
            root.isClickable = true

            cpDetailsMapButton.setOnClickListener { handleCardMapButtonClick() }
        }

        viewModel.selectedMarker.observe(viewLifecycleOwner) { handleSelectedMarkerChange(it) }
        binding.collectionPointsFilterButton.setOnClickListener { handleFilterButtonClick() }
        binding.collectionPointsMyLocationButton.setOnClickListener { handleMyLocationButtonClick() }
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
            getString(R.string.material_construction_waste),
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

    private fun handleMyLocationButtonClick() {
        if (viewModel.hasLocationPermission()) {
            getLocationAndMoveMap()
            return
        }

        /*
         * API 31+ allows users to grant only approximate location, even when the app requests the
         * ACCESS_FINE_LOCATION permission. To handle this possibility, both ACCESS_COARSE_LOCATION
         * and ACCESS_FINE_LOCATION permissions needs to be requested at the same time.
         */
        requestPermissionsLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestPermissionsCallback(returnedPermissions: MutableMap<String, Boolean>) {
        if (returnedPermissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            returnedPermissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            @SuppressLint("MissingPermission")
            viewModel.map?.isMyLocationEnabled = true
            getLocationAndMoveMap()
        } else {
            Log.d("CollectionPoints", "Location permission denied.")
            Snackbar.make(
                requireView(),
                "You must allow access to location in order to use this feature.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun getLocationAndMoveMap() {
        @SuppressLint("MissingPermission")
        val locationResult = fusedLocationClient.lastLocation
        locationResult.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                viewModel.lastKnownLocation = task.result ?: viewModel.lastKnownLocation

                if (viewModel.lastKnownLocation != null) {
                    moveMapToKnownLocation()
                } else {
                    Log.d("CollectionPoints", "Location unavailable.")
                    Snackbar.make(requireView(), "Location unavailable.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            } else {
                Log.d("CollectionPoints", "Could not get device location.")
                Snackbar.make(
                    requireView(),
                    "Could not get device location.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun moveMapToKnownLocation() {
        val location = viewModel.lastKnownLocation ?: return
        val latLng = LatLng(location.latitude, location.longitude)
        val zoom = if (location.accuracy > 500f) DISTANT_ZOOM_LEVEL else CLOSE_ZOOM_LEVEL

        viewModel.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    companion object {
        private const val CLOSE_ZOOM_LEVEL = 15f
        private const val DISTANT_ZOOM_LEVEL = 13f
    }
}
