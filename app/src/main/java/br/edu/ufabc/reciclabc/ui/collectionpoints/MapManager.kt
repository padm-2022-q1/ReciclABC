package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapManager(
    private val viewModel: CollectionPointsViewModel,
    private val activityResultRegistry: ActivityResultRegistry,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val notifyUser: (String) -> Unit
) : DefaultLifecycleObserver {

    private var map: GoogleMap? = null
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

        addMarkers(receivedMap)
        map = receivedMap
    }

    /**
     * Requests location permission if not granted and moves map to device's last known location.
     */
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

    private fun requestPermissionsCallback(returnedPermissions: MutableMap<String, Boolean>) {
        if (returnedPermissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == false &&
            returnedPermissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == false
        ) {
            Log.d("MapManager", "Location permission denied.")
            notifyUser("You must allow access to location in order to use this feature.")
            return
        }

        @SuppressLint("MissingPermission")
        map?.isMyLocationEnabled = true
        getLocationAndMoveMap()
    }

    private fun getLocationAndMoveMap() {
        @SuppressLint("MissingPermission")
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModel.lastKnownLocation = task.result ?: viewModel.lastKnownLocation

                if (viewModel.lastKnownLocation != null) {
                    moveMapToKnownLocation()
                } else {
                    Log.d("CollectionPoints", "Location unavailable.")
                    notifyUser("Location unavailable.")
                }
            } else {
                Log.d("CollectionPoints", "Could not get device location.")
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

    private fun addMarkers(map: GoogleMap) {
        for (collectionPoint in viewModel.getAllCollectionPoints()) {
            map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        collectionPoint.lat.toDouble(),
                        collectionPoint.lng.toDouble()
                    )
                ).title(collectionPoint.name)
            )?.tag = collectionPoint.id
        }
    }

    private val handleMarkerClick = GoogleMap.OnMarkerClickListener {
        val markerId = it.tag
        if (markerId is Int) {
            Log.d("CollectionPoints", "Selected marker with id $markerId")
            viewModel.selectedMarker.value = markerId
        }

        /*
         * Returns false to allow the default behaviour of moving
         * the map to center the marker and show its title
         */
        false
    }

    private val handleMapClick = GoogleMap.OnMapClickListener {
        Log.d("CollectionPoints", "Cleared selected marker")
        viewModel.selectedMarker.value = null
    }
}
