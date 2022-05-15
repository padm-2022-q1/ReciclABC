package br.edu.ufabc.reciclabc.model

data class CollectionPoint(
    val id: Int,
    val lat: Float,
    val lng: Float,
    val name: String,
    val address: String,
    val materials: Set<MaterialType>,
)
