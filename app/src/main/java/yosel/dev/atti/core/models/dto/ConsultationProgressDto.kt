package yosel.dev.atti.core.models.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

@Serializable
data class StepReferenceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("status") val status: Int = 1,
)

object StepReferenceListSerializer : KSerializer<List<StepReferenceDto>> {
    private val delegate = ListSerializer(StepReferenceDto.serializer())
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: List<StepReferenceDto>) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): List<StepReferenceDto> {
        val jsonDecoder = (decoder as? JsonDecoder)
            ?: return delegate.deserialize(decoder)
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> emptyList()
            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(delegate, element)
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(StepReferenceDto.serializer(), element))
            else -> emptyList()
        }
    }
}

@Serializable
data class ConsultationProgressDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: Int = 1,
    @SerialName("consultation_type_id") val consultationTypeId: Int? = null,
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("anamnesis") val anamnesis: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("clinical_examinations") val clinicalExaminations: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("physiological_constants") val physiologicalConstants: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("auxiliary_tests") val auxiliaryTests: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("diagnoses") val diagnoses: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("treatments") val treatments: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("prescriptions") val prescriptions: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("observations") val observations: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("follow_ups") val followUps: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("receipts") val receipts: List<StepReferenceDto> = emptyList(),
    @Serializable(with = StepReferenceListSerializer::class)
    @SerialName("fasting") val fasting: List<StepReferenceDto> = emptyList(),
)
