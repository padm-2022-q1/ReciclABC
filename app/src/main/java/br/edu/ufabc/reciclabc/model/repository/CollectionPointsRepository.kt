package br.edu.ufabc.reciclabc.model.repository

import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.MaterialType
import com.beust.klaxon.Klaxon
import java.io.InputStream

class CollectionPointsRepository {
    private lateinit var collectionPoints: List<CollectionPoint>
//        CollectionPoint(
//            1,
//            -23.644837f,
//            -46.528047f,
//            "UFABC SA",
//            "Av. dos Estados, 5001 - Santo André - SP",
//            MaterialType.values().toSet(),
//        ),
//        CollectionPoint(
//            2,
//            -23.678312f,
//            -46.563312f,
//            "UFABC SBC",
//            "Alameda da Universidade, s/n - São Bernardo do Campo - SP",
//            setOf(MaterialType.GLASS, MaterialType.METAL),
//        ),
//    )

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
