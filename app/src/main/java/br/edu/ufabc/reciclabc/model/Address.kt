package br.edu.ufabc.reciclabc.model

data class Address(
    val id: Long,
    val name: String,
    val notifications: List<Notification>,
) {

    val regularGarbage: List<Notification>
        get() = notifications.filter { n -> n.category == GarbageType.REGULAR }

    val recyclableGarbage: List<Notification>
        get() = notifications.filter { n -> n.category == GarbageType.RECYCLABLE }
}
