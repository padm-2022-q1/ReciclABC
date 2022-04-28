package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.CollectionPointsRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CollectionPointsViewModel(application: Application) : AndroidViewModel(application) {
    val selectedMarker = MutableLiveData<Int?>(null)
    var lastKnownLocation: Location? = null
    var map: GoogleMap? = null
    private val repository = CollectionPointsRepository()

    val handleMapReady = OnMapReadyCallback { googleMap ->
        Log.d("CollectionPoints", "Map ready")
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        googleMap.setOnMarkerClickListener(handleMarkerClick)
        googleMap.setOnMapClickListener(handleMapClick)

        if (hasLocationPermission()) {
            @SuppressLint("MissingPermission")
            googleMap.isMyLocationEnabled = true
        }

        addMarkers(googleMap)
        map = googleMap
    }

    private val handleMarkerClick = GoogleMap.OnMarkerClickListener {
        val markerId = it.tag
        if (markerId is Int) {
            Log.d("CollectionPoints", "Selected marker with id $markerId")
            selectedMarker.value = markerId
        }
        false
    }

    private val handleMapClick = GoogleMap.OnMapClickListener {
        Log.d("CollectionPoints", "Cleared selected marker")
        selectedMarker.value = null
    }

    private fun addMarkers(map: GoogleMap) {
        for (collectionPoint in repository.getAll()) {
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

    fun getCollectionPointById(id: Int): CollectionPoint? = repository.getById(id)

    fun hasLocationPermission(): Boolean {
        val preciseLocationPermission = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        val approximateLocationPermission = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return preciseLocationPermission == PackageManager.PERMISSION_GRANTED ||
                approximateLocationPermission == PackageManager.PERMISSION_GRANTED
    }
}
