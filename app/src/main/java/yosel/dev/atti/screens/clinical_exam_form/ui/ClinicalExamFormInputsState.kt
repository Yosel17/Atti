package yosel.dev.atti.screens.clinical_exam_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClinicalExamLymphNodeModel
import yosel.dev.atti.core.models.model.ClinicalExaminationModel
import yosel.dev.atti.core.utils.Constants

data class ClinicalExamFormInputsState(
    val mucousMembranes: String = "Rosadas",
    val isLymphNodesInfarted: Boolean = false,
    val selectedLymphNodes: List<AppCatalogModel> = emptyList(),
    val selectedCoat: AppCatalogModel? = null,
    val abdominalPalpation: String = "",
    val bodyCondition: Int = 3,
    val otherFindings: String = ""
) {
    fun toClinicalExaminationModel(consultationId: String = "") = ClinicalExaminationModel(
        consultationId = consultationId,
        mucousMembranes = mucousMembranes.trim(),
        coatCatalogId = selectedCoat?.id,
        abdominalPalpation = abdominalPalpation.trim(),
        bodyCondition = bodyCondition,
        otherFindings = otherFindings.trim(),
        status = Constants.ACTIVE_STATUS
    )

    fun toLymphNodeModels(examId: String = ""): List<ClinicalExamLymphNodeModel> {
        if (!isLymphNodesInfarted) return emptyList()
        return selectedLymphNodes.map { catalog ->
            ClinicalExamLymphNodeModel(
                clinicalExaminationId = examId,
                catalogId = catalog.id
            )
        }
    }

    fun toUpdateModel(
        examId: String,
        consultationId: String,
        createdAt: String = "",
        status: Int = Constants.ACTIVE_STATUS
    ) = ClinicalExaminationModel(
        id = examId,
        consultationId = consultationId,
        mucousMembranes = mucousMembranes.trim(),
        coatCatalogId = selectedCoat?.id,
        abdominalPalpation = abdominalPalpation.trim(),
        bodyCondition = bodyCondition,
        otherFindings = otherFindings.trim(),
        createdAt = createdAt,
        status = status
    )

    fun hasChangesFrom(initial: ClinicalExamFormInputsState): Boolean {
        return mucousMembranes != initial.mucousMembranes ||
                isLymphNodesInfarted != initial.isLymphNodesInfarted ||
                selectedLymphNodes != initial.selectedLymphNodes ||
                selectedCoat?.id != initial.selectedCoat?.id ||
                abdominalPalpation.trim() != initial.abdominalPalpation.trim() ||
                bodyCondition != initial.bodyCondition ||
                otherFindings.trim() != initial.otherFindings.trim()
    }
}
