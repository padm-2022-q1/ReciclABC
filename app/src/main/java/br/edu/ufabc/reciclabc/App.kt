package br.edu.ufabc.reciclabc

import android.app.Application
import br.edu.ufabc.reciclabc.model.repository.AddressNotificationRepository
import br.edu.ufabc.reciclabc.model.repository.CollectionPointsRepository
import br.edu.ufabc.reciclabc.model.repository.RecyclingInfoRepository
import com.google.android.gms.maps.MapsInitializer

class App : Application() {

    /**
     * Access to Repository instance.
     */
    val recyclingInfoRepository = RecyclingInfoRepository()
    val addressNotificationRepository = AddressNotificationRepository()
    val collectionPointsRepository = CollectionPointsRepository()

    companion object {
        private var recyclingGuideFile = "recycling_info.json"
        private var addressNotificationFile = "notifications.json"
        private var collectionPointsFile = "collection_points.json"
    }

    override fun onCreate() {
        super.onCreate()

        // Enables the new map renderer
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, null)

        resources.assets.open(recyclingGuideFile).use {
            recyclingInfoRepository.loadData(it)
        }

        resources.assets.open(addressNotificationFile).use {
            addressNotificationRepository.loadData(it)
        }

        resources.assets.open(collectionPointsFile).use {
            collectionPointsRepository.loadData(it)
        }
    }
}
