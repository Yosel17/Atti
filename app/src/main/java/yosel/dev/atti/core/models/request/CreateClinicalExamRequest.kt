package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.ClinicalExamLymphNodeDto
import yosel.dev.atti.core.models.dto.ClinicalExaminationDto

@Serializable
data class CreateClinicalExamRequest(
    @SerialName("clinical_exam_data")
    val clinicalExamData: ClinicalExaminationDto,
    @SerialName("lymph_nodes_data")
    val lymphNodesData: List<ClinicalExamLymphNodeDto> = emptyList()
)
