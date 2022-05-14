package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.edu.ufabc.reciclabc.model.CollectionPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapManager(
    private val viewModel: CollectionPointsViewModel,
    private val activityResultRegistry: ActivityResultRegistry,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val notifyUser: (String) -> Unit
) : DefaultLifecycleObserver {

    private var map: GoogleMap? = null
    private val markers = mutableListOf<Marker>()
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    companion object {
        private const val REQUEST_LOCATION_PERMISSION_RESULT_KEY = "requestLocationPermissionKey"
        private const val CLOSE_ZOOM_LEVEL = 15f
        private const val DISTANT_ZOOM_LEVEL = 13f
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
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestPermissionsCallback(returnedPermissions: MutableMap<String, Boolean>) {
        if (returnedPermissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == false &&
            returnedPermissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == false
        ) {
            Log.d("MapManager", "Location permission denied.")
            notifyUser("You must allow access to location in order to use this feature.")
            return
        }

        map?.isMyLocationEnabled = true
        getLocationAndMoveMap()
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private fun getLocationAndMoveMap() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModel.lastKnownLocation = task.result ?: viewModel.lastKnownLocation

                if (viewModel.lastKnownLocation != null) {
                    moveMapToKnownLocation()
                } else {
                    Log.d("MapManager", "Location unavailable.")
                    notifyUser("Location unavailable.")
                }
            } else {
                Log.d("MapManager", "Could not get device location.")
                notifyUser("Could not get device location.")
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
        val markerId = it.tag
        if (markerId is Int) {
            Log.d("MapManager", "Selected marker with id $markerId")
            viewModel.selectedMarker.value = markerId
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
    }
}
