package yosel.dev.atti.screens.navigation_bar.consultation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.utils.getIconForConsultationReason
import yosel.dev.atti.core.utils.getIconSpecies
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.ui.theme.AttiTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyConsultation(
    modifier: Modifier = Modifier,
    state: ConsultationState,
    onAction: (ConsultationAction) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. Header de Estado / Sección Pacientes ---
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (state.hasActiveConsultation) "Consulta en Proceso" else "Seleccionar Paciente",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (!state.hasActiveConsultation) {
                    AttiSearchBar(
                        value = state.patientSearchQuery,
                        onValueChange = { onAction(ConsultationAction.OnSearchPatientQueryChange(it)) },
                        placeholder = "Buscar por nombre o dueño...",
                        onFilterClick = {}
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // --- 2. Carrusel Horizontal de Pacientes ---
        item(span = { GridItemSpan(2) }) {
            val displayPatients = remember(
                state.hasActiveConsultation,
                state.selectedPatient,
                state.filteredPatients
            ) {
                if (state.hasActiveConsultation && state.selectedPatient != null) {
                    listOf(state.selectedPatient)
                } else {
                    state.filteredPatients
                }
            }

            AnimatedContent(
                targetState = displayPatients.isEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 220)) togetherWith
                            fadeOut(animationSpec = tween(durationMillis = 180))
                },
                label = "PatientsEmptyStateTransition"
            ) { isEmpty ->
                if (isEmpty) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Pets,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No se encontraron pacientes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(
                            items = displayPatients,
                            key = { it.patient.id }
                        ) { patientItem ->
                            val isSelected = state.selectedPatient?.patient?.id == patientItem.patient.id

                            PatientAvatarItem(
                                modifier = Modifier.animateItem(),
                                patientItem = patientItem,
                                isSelected = isSelected,
                                isLocked = state.hasActiveConsultation,
                                onClick = {
                                    onAction(ConsultationAction.OnSelectPatient(patientItem))
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Header de Motivos de Consulta ---
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(
                    text = "Motivo de Consulta",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // --- 4. Grid de Motivos de Consulta ---
        items(state.consultationReasons, key = { it.id }) { reason ->
            val isSelected = state.selectedReason?.id == reason.id
            val isEmergency = reason.name.normalize().contains("emergencia") || reason.name.normalize().contains("urgencia")

            ConsultationReasonCard(
                reason = reason,
                isSelected = isSelected,
                isEmergency = isEmergency,
                isLocked = state.hasActiveConsultation,
                onClick = {
                    onAction(ConsultationAction.OnSelectConsultationReason(reason))
                }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PatientAvatarItem(
    modifier: Modifier = Modifier,
    patientItem: PatientWithCatalogsModel,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "BorderColorAnim"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isLocked, onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) borderColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getIconSpecies(patientItem.patient.speciesId)),
                    contentDescription = patientItem.species.name,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
        ) {
            Box(
                modifier = Modifier.padding(6.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = patientItem.patient.name.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }

    }
}

@Composable
fun ConsultationReasonCard(
    modifier: Modifier = Modifier,
    reason: AppCatalogModel,
    isSelected: Boolean,
    isEmergency: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val icon = remember(reason.name) { getIconForConsultationReason(reason.name) }

    val containerColor = when {
        isSelected && isEmergency -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }

    val contentColor = when {
        isSelected && isEmergency -> MaterialTheme.colorScheme.onErrorContainer
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isEmergency -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, if (isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        isEmergency -> BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = reason.name,
                tint = contentColor,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = reason.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                ),
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ConfirmStartConsultationDialog(
    patientName: String,
    reasonName: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "¿Iniciar Consulta?",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Se registrará e iniciará la consulta de tipo \"$reasonName\" para el paciente \"$patientName\".",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                shape = RoundedCornerShape(100.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Iniciando...")
                } else {
                    Text(text = "Iniciar consulta")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(text = "Cancelar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(28.dp)
    )
}

@PreviewLightDark
@Composable
private fun PatientAvatarItemPreview() {
    AttiTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)
        ){
            PatientAvatarItem(
                patientItem = PatientWithCatalogsModel(
                    patient = PatientModel(
                        name = "Neron",
                        speciesId = 1
                    )
                ),
                isSelected = true,
                isLocked = false,
                onClick = {}
            )
        }
    }
}