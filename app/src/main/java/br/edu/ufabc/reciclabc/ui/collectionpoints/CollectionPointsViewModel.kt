package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.CollectionPointsRepository

class CollectionPointsViewModel(application: Application) : AndroidViewModel(application) {
    val selectedMarker = MutableLiveData<Int?>(null)
    var lastKnownLocation: Location? = null
    private val repository = CollectionPointsRepository()

    fun getAllCollectionPoints(): List<CollectionPoint> = repository.getAll()

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
