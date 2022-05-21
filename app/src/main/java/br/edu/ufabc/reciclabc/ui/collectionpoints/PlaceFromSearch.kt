package br.edu.ufabc.reciclabc.ui.collectionpoints

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class PlaceFromSearch(val name: String, val address: String, val latLng: LatLng) {
    private var marker: Marker? = null
    fun drawMarker(map: GoogleMap) {
        map.addMarker(
            MarkerOptions().position(latLng).title(name).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            ).zIndex(1f)
        )?.let { newMarker ->
            newMarker.tag = MARKER_TAG
            marker = newMarker
        }
    }

    fun removeMarker() {
        marker?.remove()
    }

    companion object {
        const val MARKER_TAG = "PLACE_MARKER_TAG"
    }
}
