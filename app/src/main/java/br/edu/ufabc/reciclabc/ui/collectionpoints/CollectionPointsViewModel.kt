package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.CollectionPointsRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CollectionPointsViewModel : ViewModel() {
    val selectedMarker = MutableLiveData<Int?>(null)
    private val repository = CollectionPointsRepository()

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
        for (collectionPoint in repository.getAll()) {
            map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        collectionPoint.lat.toDouble(),
                        collectionPoint.lng.toDouble()
                    )
                )
            )?.tag = collectionPoint.id
        }
    }

    fun getCollectionPointById(id: Int): CollectionPoint? = repository.getById(id)
}
