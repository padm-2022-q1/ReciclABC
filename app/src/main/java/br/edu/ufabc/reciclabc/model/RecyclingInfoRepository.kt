package br.edu.ufabc.reciclabc.model

import com.beust.klaxon.Klaxon
import java.io.InputStream

class RecyclingInfoRepository {
    private lateinit var recyclingInfo: List<RecyclingInformation>

    /**
     * Read Json File.
     */
    fun loadData(inputStream: InputStream) {
        recyclingInfo = Klaxon().parseArray(inputStream) ?: emptyList()
        recyclingInfo = recyclingInfo.sortedBy { info -> info.title }
    }

    fun getAll() = if (this::recyclingInfo.isInitialized) recyclingInfo
    else throw UninitializedPropertyAccessException("Load data first")

    fun getById(id: Long) = if (this::recyclingInfo.isInitialized) recyclingInfo.find { info ->
        info.id == id
    } else throw UninitializedPropertyAccessException("Load data first")

}