package br.edu.ufabc.reciclabc.model

import com.beust.klaxon.Klaxon
import java.io.InputStream

class Repository {
    private lateinit var recycling_info: List<RecyclingInformation>

    /**
     * Read Json File.
     */
    fun loadData(inputStream: InputStream) {
        recycling_info = Klaxon().parseArray(inputStream) ?: emptyList()
        recycling_info = recycling_info.sortedBy { info -> info.title }
    }

    fun getAll() = if (this::recycling_info.isInitialized) recycling_info
        else throw UninitializedPropertyAccessException("Load data first")

    fun getById(id: Long) = if (this::recycling_info.isInitialized) recycling_info.find { info ->
        info.id == id
    } else throw UninitializedPropertyAccessException("Load data first")
}