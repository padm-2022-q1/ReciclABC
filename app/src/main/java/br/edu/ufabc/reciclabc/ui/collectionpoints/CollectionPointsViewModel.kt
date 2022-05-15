package br.edu.ufabc.reciclabc.ui.collectionpoints

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.MaterialType
import br.edu.ufabc.reciclabc.model.repository.CollectionPointsRepository

class CollectionPointsViewModel(application: Application) : AndroidViewModel(application) {
    val selectedMarker = MutableLiveData<Int?>(null)
    val placeFromSearch = MutableLiveData<PlaceFromSearch?>(null)
    var lastKnownLocation: Location? = null
    private val repository = CollectionPointsRepository()

    private val _collectionPoints = MutableLiveData(repository.getAll())
    val collectionPoints: LiveData<List<CollectionPoint>> = _collectionPoints

    private val _materialFilter = MutableLiveData<MutableSet<MaterialType>>(mutableSetOf())
    val materialFilter: LiveData<MutableSet<MaterialType>> = _materialFilter

    fun clearMaterialFilter() {
        _materialFilter.value = mutableSetOf()
    }

    fun addMaterialFilterOption(material: MaterialType) {
        _materialFilter.value?.add(material)
    }

    fun removeMaterialFilterOption(material: MaterialType) {
        _materialFilter.value?.remove(material)
    }

    fun filterCollectionPoints() {
        _materialFilter.value?.let { materials ->
            _collectionPoints.value = repository.getAll().filter { collectionPoint ->
                collectionPoint.materials.containsAll(materials)
            }
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
