package br.edu.ufabc.reciclabc.model.repository

import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.MaterialType
import com.beust.klaxon.Klaxon
import java.io.InputStream

class CollectionPointsRepository {
    private lateinit var collectionPoints: List<CollectionPoint>

    private data class CollectionPointJson(
        val id: Int,
        val lat: String,
        val lng: String,
        val name: String,
        val address: String,
        val materials: String
    ) {
        fun toCollectionPoints() = CollectionPoint(
            id,
            lat.toFloat(),
            lng.toFloat(),
            name,
            address,
            materials.split(",").map { MaterialType.valueOf(it) }.toSet()
        )
    }

    fun loadData(inputStream: InputStream) {
        collectionPoints = Klaxon().parseArray<CollectionPointJson>(inputStream)?.map {
            it.toCollectionPoints()
        }?: emptyList()
    }

    fun getAll(): List<CollectionPoint> =
        if (this::collectionPoints.isInitialized) collectionPoints
        else throw UninitializedPropertyAccessException("Load data first")

    fun getById(id: Int): CollectionPoint? =
        if (this::collectionPoints.isInitialized) collectionPoints.find { it.id == id }
        else throw UninitializedPropertyAccessException("Load data first")
}
