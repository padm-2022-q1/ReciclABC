package br.edu.ufabc.reciclabc.model.repository

import br.edu.ufabc.reciclabc.model.CollectionPoint
import br.edu.ufabc.reciclabc.model.MaterialType

class CollectionPointsRepository {
    private val collectionPoints: List<CollectionPoint> = listOf(
        CollectionPoint(
            1,
            -23.644837f,
            -46.528047f,
            "UFABC SA",
            "Av. dos Estados, 5001 - Santo André - SP",
            MaterialType.values().toSet(),
        ),
        CollectionPoint(
            2,
            -23.678312f,
            -46.563312f,
            "UFABC SBC",
            "Alameda da Universidade, s/n - São Bernardo do Campo - SP",
            setOf(MaterialType.GLASS, MaterialType.METAL),
        ),
    )

    fun getAll(): List<CollectionPoint> = collectionPoints
    fun getById(id: Int): CollectionPoint? = collectionPoints.find { it.id == id }
}
