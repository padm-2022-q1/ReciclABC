package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class MapManager(
    private val context: Context,
    private val viewModel: CollectionPointsViewModel,
    private val activityResultRegistry: ActivityResultRegistry,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val notifyUser: (String) -> Unit
) : DefaultLifecycleObserver {

    private var map: GoogleMap? = null
    private val markers = mutableListOf<Marker>()
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var placeAutocompleteSearchLauncher: ActivityResultLauncher<Intent>

    companion object {
        private const val REQUEST_LOCATION_PERMISSION_RESULT_KEY = "requestLocationPermissionKey"
        private const val PLACE_AUTOCOMPLETE_SEARCH_RESULT_KEY = "placeAutocompleteSearchKey"
        private const val CLOSE_ZOOM_LEVEL = 15f
        private const val DISTANT_ZOOM_LEVEL = 13f
        private val ABC_CENTER_BOX_NORTHEAST = LatLng(-23.6, -46.5)
        private val ABC_CENTER_BOX_SOUTHWEST = LatLng(-23.7, -46.6)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        requestPermissionsLauncher = activityResultRegistry.register(
            REQUEST_LOCATION_PERMISSION_RESULT_KEY,
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            requestPermissionsCallback(permissions)
        }

        placeAutocompleteSearchLauncher = activityResultRegistry.register(
            PLACE_AUTOCOMPLETE_SEARCH_RESULT_KEY,
            owner,
            ActivityResultContracts.StartActivityForResult()
        ) {
            placeAutocompleteSearchCallback(it)
        }

        viewModel.placeFromSearch.observe(owner) {
            map?.let { map -> it?.drawMarker(map) }
        }
    }

    /**
     * To be called when the map is ready.
     */
    fun mapReady(receivedMap: GoogleMap) {
        receivedMap.uiSettings.isMapToolbarEnabled = false
        receivedMap.uiSettings.isMyLocationButtonEnabled = false

        receivedMap.setOnMarkerClickListener(handleMarkerClick)
        receivedMap.setOnMapClickListener(handleMapClick)

        if (viewModel.hasLocationPermission()) {
            @SuppressLint("MissingPermission")
            receivedMap.isMyLocationEnabled = true
        }

        viewModel.placeFromSearch.value?.drawMarker(receivedMap)
        map = receivedMap
    }

    /**
     * Requests location permission if not granted and moves map to device's last known location.
     */
    @SuppressLint("MissingPermission")
    fun goToCurrentLocation() {
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
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Opens an overlay autocomplete field to search for an address.
     */
    fun openPlaceSearch() {
        val fieldsToReturn =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsToReturn)
                .setTypeFilter(TypeFilter.ADDRESS)
                .let {
                    viewModel.placeFromSearch.value?.address.let { previousAddress ->
                        it.setInitialQuery(previousAddress)
                    }
                }
                .setLocationBias(
                    RectangularBounds.newInstance(
                        ABC_CENTER_BOX_SOUTHWEST,
                        ABC_CENTER_BOX_NORTHEAST
                    )
                )
                .build(context)

        placeAutocompleteSearchLauncher.launch(intent)
    }

    @SuppressLint("MissingPermission")
    private fun requestPermissionsCallback(returnedPermissions: MutableMap<String, Boolean>) {
        if (returnedPermissions[Manifest.permission.ACCESS_FINE_LOCATION] == false &&
            returnedPermissions[Manifest.permission.ACCESS_COARSE_LOCATION] == false
        ) {
            Log.d("MapManager", "Location permission denied.")
            notifyUser(context.getString(R.string.error_location_access_required))
            return
        }

        map?.isMyLocationEnabled = true
        getLocationAndMoveMap()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun getLocationAndMoveMap() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModel.lastKnownLocation = task.result ?: viewModel.lastKnownLocation

                if (viewModel.lastKnownLocation != null) {
                    moveMapToKnownLocation()
                } else {
                    Log.d("MapManager", "Location unavailable.")
                    notifyUser(context.getString(R.string.error_location_unavailable))
                }
            } else {
                Log.d("MapManager", "Could not get device location.")
                notifyUser(context.getString(R.string.error_could_not_get_location))
            }
        }
    }

    private fun moveMapToKnownLocation() {
        val location = viewModel.lastKnownLocation ?: return
        val latLng = LatLng(location.latitude, location.longitude)
        val zoom = if (location.accuracy > 500f) DISTANT_ZOOM_LEVEL else CLOSE_ZOOM_LEVEL

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }


    fun addMarker(collectionPoint: CollectionPoint) {
        map?.let {
            val marker = it.addMarker(collectionPointToMarkerOption(collectionPoint))
            if (marker != null) {
                marker.tag = collectionPoint.id
                markers += marker
            }
        }
    }

    fun removeMarker(markerTag: Int) {
        val marker = markers.find { it.tag == markerTag }
        if (marker != null) {
            marker.remove()
            markers.remove(marker)
        }
    }

    private fun collectionPointToMarkerOption(collectionPoint: CollectionPoint) =
        MarkerOptions().position(
            LatLng(
                collectionPoint.lat.toDouble(),
                collectionPoint.lng.toDouble()
            )
        ).title(collectionPoint.name)

    private val handleMarkerClick = GoogleMap.OnMarkerClickListener {
        Log.d("MapManager", "selected ${it.tag}")
        when(val markerId = it.tag) {
            is Int -> {
                Log.d("MapManager", "Selected marker with id $markerId")
                viewModel.selectedMarker.value = markerId
                viewModel.placeFromSearchSelected.value = false
            }
            PlaceFromSearch.MARKER_TAG -> {
                Log.d("MapManager", "Selected place from search")
                viewModel.selectedMarker.value = null
                viewModel.placeFromSearchSelected.value = true
            }
            else -> {
                viewModel.selectedMarker.value = null
                viewModel.placeFromSearchSelected.value = false
            }
        }

        /*
         * Returns false to allow the default behaviour of moving
         * the map to center the marker and show its title
         */
        false
    }

    private val handleMapClick = GoogleMap.OnMapClickListener {
        Log.d("MapManager", "Cleared selected marker")
        viewModel.selectedMarker.value = null
        viewModel.placeFromSearchSelected.value = false
    }

    private fun placeAutocompleteSearchCallback(result: ActivityResult) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    val name = place.name
                    val address = place.address
                    val latLng = place.latLng

                    if (name == null || address == null || latLng == null) {
                        Log.e("MapManager", "Missing fields from place search response.")
                        notifyUser(context.getString(R.string.error_place_search))
                        return
                    }

                    viewModel.placeFromSearch.value?.removeMarker()
                    viewModel.placeFromSearch.value = PlaceFromSearch(name, address, latLng)
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CLOSE_ZOOM_LEVEL))
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                notifyUser(context.getString(R.string.error_place_search))
                result.data?.let { intent ->
                    val status = Autocomplete.getStatusFromIntent(intent)
                    Log.i("MapManager", "Error on place search. Status: ${status.statusMessage}")
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.d("MapManager", "Place search canceled by user")
            }
        }
    }
}
