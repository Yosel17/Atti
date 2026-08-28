package yosel.dev.atti.screens.detail_consultation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.PatientConsultationHeaderHero
import yosel.dev.atti.core.models.model.ConsultationStepProgressModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.getConsultationStepIcon
import yosel.dev.atti.core.utils.getConsultationStepScreen
import yosel.dev.atti.ui.theme.customColors

@Composable
fun BodyDetailConsultation(
    modifier: Modifier = Modifier,
    state: DetailConsultationState,
    onNavigationMain: (Screens) -> Unit,
    onFinishConsultation: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        PatientConsultationHeaderHero(consultation = state.consultationWithDetails)
        Spacer(modifier = Modifier.height(16.dp))

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
                        onClick = {
                            onNavigationMain(
                                getConsultationStepScreen(
                                    stepName = step.stepCatalog.name,
                                    consultationId = state.consultationWithDetails.consultation.id,
                                    recordId = step.recordId
                                )
                            )
                        }
                    )
                }
            }
        }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
    step: ConsultationStepProgressModel,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StepCardItem(
            stepName = step.stepCatalog.name.ifBlank { "Paso sin nombre" },
            isCompleted = step.isCompleted,
            onClick = onClick
        )
        if (!isLast) {
            Box(
                modifier = Modifier
                    .padding(start = 37.dp)
                    .width(2.dp)
                    .height(20.dp)
                    .background(
                        if (step.isCompleted) MaterialTheme.customColors.active.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                    )
            )
        }
    }
}

@Composable
private fun StepCardItem(
    stepName: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isCompleted) MaterialTheme.customColors.active.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
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
                color = if (isCompleted) MaterialTheme.customColors.activeContainer else MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Rounded.Check else getConsultationStepIcon(stepName = stepName),
                        contentDescription = if (isCompleted) "Completado" else "Pendiente",
                        modifier = Modifier.size(22.dp),
                        tint = if (isCompleted) MaterialTheme.customColors.onActiveContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stepName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (isCompleted) {
                    Text(
                        text = "Completado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.customColors.active,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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