package br.edu.ufabc.reciclabc.model

data class Address(
    val id: Long,
    val name: String,
    val notifications: List<NotificationGroup>,
) {

    val regularGarbage: List<NotificationGroup>
        get() = notifications.filter { n -> n.category == GarbageType.REGULAR }

    val recyclableGarbage: List<NotificationGroup>
        get() = notifications.filter { n -> n.category == GarbageType.RECYCLABLE }
}
