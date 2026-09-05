package yosel.dev.atti.core.room.tables.receipt

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationWithDetailsEntity

data class ReceiptWithDetailsEntity(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        entity = ConsultationEntity::class,
        parentColumn = "consultation_id",
        entityColumn = "id"
    )
    val consultation: ConsultationWithDetailsEntity? = null,
    @Relation(
        entity = ReceiptItemEntity::class,
        parentColumn = "id",
        entityColumn = "receipt_id"
    )
    val items: List<ReceiptItemWithDetailsEntity> = emptyList()
)
