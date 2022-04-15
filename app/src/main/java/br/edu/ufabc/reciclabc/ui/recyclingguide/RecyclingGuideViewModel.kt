package br.edu.ufabc.reciclabc.ui.recyclingguide

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.edu.ufabc.reciclabc.App

class RecyclingGuideViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as App).recyclingInfoRepository

    fun allRecyclingInformation() = repository.getAll()

    fun getRecyclingInformationById(id: Long) = repository.getById(id)
}