package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CollectionPointsViewModel : ViewModel() {
    private val selectedMarker = MutableLiveData<Int?>(null)

    val handleMapReady = OnMapReadyCallback { map ->
        Log.d("CollectionPoints", "Map ready")
        map.uiSettings.isMapToolbarEnabled = false

        map.setOnMarkerClickListener(handleMarkerClick)
        map.setOnMapClickListener(handleMapClick)

        addMarkers(map)
    }

    private val handleMarkerClick = GoogleMap.OnMarkerClickListener {
        val markerId = it.tag
        if (markerId is Int) {
            Log.d("CollectionPoints", "Selected marker with id $markerId")
            selectedMarker.value = markerId
        }
        true
    }

    private val handleMapClick = GoogleMap.OnMapClickListener {
        Log.d("CollectionPoints", "Cleared selected marker")
        selectedMarker.value = null
    }

    private fun addMarkers(map: GoogleMap) {
        map.addMarker(MarkerOptions().position(LatLng(-23.644837, -46.528047)))?.tag = 1
    }
}
