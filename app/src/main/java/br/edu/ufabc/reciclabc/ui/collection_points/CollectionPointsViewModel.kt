package br.edu.ufabc.reciclabc.ui.collection_points

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CollectionPointsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Collection Points Fragment"
    }
    val text: LiveData<String> = _text
}