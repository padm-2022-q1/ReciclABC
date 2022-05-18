package br.edu.ufabc.reciclabc

import android.app.Application
import br.edu.ufabc.reciclabc.model.repository.AddressNotificationRepositoryRoom
import br.edu.ufabc.reciclabc.model.repository.CollectionPointsRepository
import br.edu.ufabc.reciclabc.model.repository.RecyclingInfoRepository
import com.google.android.gms.maps.MapsInitializer

class App : Application() {

    /**
     * Access to Repository instance.
     */
    lateinit var addressNotificationRepository: AddressNotificationRepositoryRoom
    val collectionPointsRepository = CollectionPointsRepository()
    val recyclingInfoRepository = RecyclingInfoRepository()

    companion object {
        private var collectionPointsFile = "collection_points.json"
        private var recyclingGuideFile = "recycling_info.json"
    }

    override fun onCreate() {
        super.onCreate()
        addressNotificationRepository = AddressNotificationRepositoryRoom(this)

        // Enables the new map renderer
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, null)

        resources.assets.open(recyclingGuideFile).use {
            recyclingInfoRepository.loadData(it)
        }

        resources.assets.open(collectionPointsFile).use {
            collectionPointsRepository.loadData(it)
        }
    }
}
