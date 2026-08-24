package yosel.dev.atti.screens.detail_consultation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.PatientConsultationHeaderCard
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationModel
import yosel.dev.atti.core.models.model.ConsultationTypeStepModel
import yosel.dev.atti.core.models.model.ConsultationTypeStepWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.ui.theme.AttiTheme

@Composable
fun BodyDetailConsultation(
    modifier: Modifier = Modifier,
    state: DetailConsultationState,
    onStepClick: (ConsultationTypeStepWithDetailsModel) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Cabecera del Paciente
        item(key = "header_patient") {
            PatientConsultationHeaderCard(
                consultation = state.consultationWithDetails
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Título de la sección
        item(key = "title_steps") {
            SectionTitle(
                title = "Pasos de la consulta",
                icon = Icons.Outlined.FormatListNumbered
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de pasos con la línea vertical entre cards
        if (state.consultationSteps.isEmpty()) {
            item(key = "empty_steps") {
                EmptyGlobal(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    title = "Sin pasos registrados",
                    subTitle = "No se encontraron pasos configurados para este tipo de consulta."
                )
            }
        } else {
            itemsIndexed(
                items = state.consultationSteps,
                key = { _, item -> item.typeStep.id.takeIf { it != 0 } ?: item.stepCatalog.id }
            ) { index, step ->
                ConsultationTimelineStepItem(
                    step = step,
                    isLast = index == state.consultationSteps.lastIndex,
                    onClick = { onStepClick(step) }
                )
            }
        }
    }
}

@Composable
fun ConsultationTimelineStepItem(
    step: ConsultationTypeStepWithDetailsModel,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Card del paso
        StepCardItem(
            stepName = step.stepCatalog.name.ifBlank { "Paso sin nombre" },
            onClick = onClick
        )

        // Línea vertical que conecta hacia el siguiente paso
        // paddingStart de 37dp = 16dp de padding de la card + 22dp (mitad del icono de 44dp) - 1dp (mitad del grosor de 2dp)
        if (!isLast) {
            Box(
                modifier = Modifier
                    .padding(start = 37.dp)
                    .width(2.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
private fun StepCardItem(
    stepName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono circular neutral
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Nombre del paso
            Text(
                text = stepName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Icono flecha de acción
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Continuar paso",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun BodyDetailConsultationPreview() {
    AttiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BodyDetailConsultation(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                state = DetailConsultationState(
                    isLoading = false,
                    consultationWithDetails = ConsultationWithDetailsModel(
                        consultation = ConsultationModel(
                            id = "mock-consultation-1",
                            patientId = "patient-1",
                            consultationTypeId = 23,
                            startedAt = "2026-08-24 10:30:00.000000+00",
                            status = 1
                        ),
                        patientWithDetails = PatientWithCatalogsModel(
                            patient = PatientModel(
                                id = "patient-1",
                                name = "Max",
                                breed = "Golden Retriever",
                                speciesId = 1,
                                ageYears = 3,
                                ageMonths = 2
                            ),
                            species = AppCatalogModel(
                                id = 1,
                                name = "Canino"
                            ),
                            gender = AppCatalogModel(
                                id = 5,
                                name = "Macho"
                            )
                        ),
                        consultationType = AppCatalogModel(
                            id = 23,
                            name = "Consulta General"
                        )
                    ),
                    consultationSteps = listOf(
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 1, consultationTypeId = 23, stepCatalogId = 101, stepOrder = 1),
                            stepCatalog = AppCatalogModel(id = 101, name = "Anamnesis")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 2, consultationTypeId = 23, stepCatalogId = 102, stepOrder = 2),
                            stepCatalog = AppCatalogModel(id = 102, name = "Examen Clínico")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 3, consultationTypeId = 23, stepCatalogId = 103, stepOrder = 3),
                            stepCatalog = AppCatalogModel(id = 103, name = "Constantes fisiológicas")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 4, consultationTypeId = 23, stepCatalogId = 104, stepOrder = 4),
                            stepCatalog = AppCatalogModel(id = 104, name = "Diagnóstico")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 5, consultationTypeId = 23, stepCatalogId = 105, stepOrder = 5),
                            stepCatalog = AppCatalogModel(id = 105, name = "Tratamiento y Receta")
                        )
                    )
                ),
                onStepClick = {}
            )
        }
    }
}