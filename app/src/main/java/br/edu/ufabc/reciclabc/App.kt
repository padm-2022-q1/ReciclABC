package br.edu.ufabc.reciclabc

import android.app.Application
import br.edu.ufabc.reciclabc.model.Repository

class App : Application() {

    /**
     * Access to Repository instance.
     */
    val repository = Repository()

    companion object {
        private var recyclingGuideFile = "recycling_info.json"
    }

    override fun onCreate() {
        super.onCreate()

        resources.assets.open(recyclingGuideFile).use {
            repository.loadData(it)
        }
    }
}