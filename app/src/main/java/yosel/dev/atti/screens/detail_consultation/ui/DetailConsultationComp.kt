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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    onStepClick: (ConsultationTypeStepWithDetailsModel) -> Unit = {},
    onFinishConsultation: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        // 1. Cabecera fija
        PatientConsultationHeaderCard(
            consultation = state.consultationWithDetails
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Pasos con Scroll independiente
        if (state.consultationSteps.isEmpty()) {
            EmptyGlobal(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                title = "Sin pasos registrados",
                subTitle = "No se encontraron pasos configurados para esta consulta."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp)
            ) {
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

        // 3. Botón fijo inferior
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onFinishConsultation,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Finalizar Consulta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
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
        StepCardItem(
            stepName = step.stepCatalog.name.ifBlank { "Paso sin nombre" },
            onClick = onClick
        )

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
                            status = 1
                        ),
                        patientWithDetails = PatientWithCatalogsModel(
                            patient = PatientModel(
                                id = "patient-1",
                                name = "Max",
                                breed = "Golden Retriever",
                                speciesId = 1
                            ),
                            species = AppCatalogModel(
                                id = 1,
                                name = "En Consulta"
                            )
                        ),
                        consultationType = AppCatalogModel(
                            id = 23,
                            name = "Consulta General"
                        )
                    ),
                    consultationSteps = listOf(
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 1),
                            stepCatalog = AppCatalogModel(id = 1, name = "Anamnesis")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 2),
                            stepCatalog = AppCatalogModel(id = 2, name = "Examen Clínico")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 3),
                            stepCatalog = AppCatalogModel(id = 3, name = "Constantes fisiológicas")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 4),
                            stepCatalog = AppCatalogModel(id = 4, name = "Diagnóstico")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 5),
                            stepCatalog = AppCatalogModel(id = 5, name = "Pruebas auxiliares")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 6),
                            stepCatalog = AppCatalogModel(id = 6, name = "Tratamiento")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 7),
                            stepCatalog = AppCatalogModel(id = 7, name = "Receta")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 8),
                            stepCatalog = AppCatalogModel(id = 8, name = "Observaciones")
                        ),
                        ConsultationTypeStepWithDetailsModel(
                            typeStep = ConsultationTypeStepModel(id = 9),
                            stepCatalog = AppCatalogModel(id = 9, name = "Reconsulta")
                        )
                    )
                )
            )
        }
    }
}