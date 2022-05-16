package br.edu.ufabc.reciclabc

import android.app.Application
import br.edu.ufabc.reciclabc.model.repository.AddressNotificationRepositoryRoom
import br.edu.ufabc.reciclabc.model.repository.RecyclingInfoRepository
import com.google.android.gms.maps.MapsInitializer

class App : Application() {

    /**
     * Access to Repository instance.
     */
    val recyclingInfoRepository = RecyclingInfoRepository()
    val addressNotificationRepository = AddressNotificationRepositoryRoom(this)

    companion object {
        private var recyclingGuideFile = "recycling_info.json"
    }

    override fun onCreate() {
        super.onCreate()

        // Enables the new map renderer
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, null)

        resources.assets.open(recyclingGuideFile).use {
            recyclingInfoRepository.loadData(it)
        }
    }
}
