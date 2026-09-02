package yosel.dev.atti.core.room.tables.prescription

import androidx.room.Embedded
import androidx.room.Relation

data class PrescriptionWithDetailsEntity(
    @Embedded val prescription: PrescriptionEntity,
    @Relation(
        entity = PrescriptionItemEntity::class,
        parentColumn = "id",
        entityColumn = "prescription_id"
    )
    val items: List<PrescriptionItemWithDetailsEntity> = emptyList()
)
