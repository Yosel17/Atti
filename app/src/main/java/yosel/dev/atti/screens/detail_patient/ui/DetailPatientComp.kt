package yosel.dev.atti.screens.detail_patient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.StatusChip
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.utils.getGenderInfo
import yosel.dev.atti.core.utils.getSpeciesInfo
import yosel.dev.atti.ui.theme.AttiTheme

@Composable
fun BodyDetailPatient(
    modifier: Modifier = Modifier,
    state: DetailPatientState
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            PatientProfileHeader(
                patientWithCatalogs = state.patientWithCatalogs
            )
        }
        item {
            PatientInformationCard(
                patientWithCatalogs = state.patientWithCatalogs,
                client = state.client
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 2. Header con Icono de Especie, Borde, Badge de Verificado y Nombre del Paciente
 */
@Composable
private fun PatientProfileHeader(
    patientWithCatalogs: PatientWithCatalogsModel,
    modifier: Modifier = Modifier
) {
    val speciesInfo = getSpeciesInfo(patientWithCatalogs.patient.speciesId)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            // Círculo principal con borde
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = speciesInfo.icon),
                    contentDescription = speciesInfo.label,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Mini círculo con icono de verificado
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                tonalElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = "Verificado",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Nombre del paciente
        Text(
            text = patientWithCatalogs.patient.name.ifBlank { "Sin nombre" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 3. Card con toda la información detallada del paciente y dueño
 */
@Composable
private fun PatientInformationCard(
    patientWithCatalogs: PatientWithCatalogsModel,
    client: ClientModel,
    modifier: Modifier = Modifier
) {
    val speciesInfo = getSpeciesInfo(patientWithCatalogs.patient.speciesId)
    val genderInfo = getGenderInfo(patientWithCatalogs.patient.genderId)

    val ownerName = "${client.firstName} ${client.lastName}".trim().ifBlank { "Sin información" }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Edad
            DetailRow(
                icon = Icons.Outlined.Event,
                label = "Edad",
                value = patientWithCatalogs.patient.formattedAge
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Especie
            DetailRow(
                icon = Icons.Outlined.Category,
                label = "Especie",
                value = patientWithCatalogs.species.name
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Raza
            DetailRow(
                icon = Icons.Outlined.Fingerprint,
                label = "Raza",
                value = patientWithCatalogs.patient.breed.ifBlank { "Sin información" }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Género
            DetailRow(
                icon = genderInfo.icon,
                label = "Género",
                value = patientWithCatalogs.gender.name
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Castrado / Esterilizado
            DetailRow(
                icon = Icons.Outlined.ContentCut,
                label = "Castrado",
                value = if (patientWithCatalogs.patient.isNeutered) "Sí" else "No",
                valueIcon = if (patientWithCatalogs.patient.isNeutered) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
                colorValue = if (patientWithCatalogs.patient.isNeutered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                colorIconValue = if (patientWithCatalogs.patient.isNeutered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Color de pelaje
            DetailRow(
                icon = Icons.Outlined.ColorLens,
                label = "Color de pelaje",
                value = patientWithCatalogs.patient.color.ifBlank { "Sin información" }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Propietario
            DetailRow(
                icon = Icons.Outlined.Person,
                label = "Propietario",
                value = ownerName
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            DetailRow(
                icon = Icons.Outlined.Info,
                label = "Estado",
                value = "",
                valueComposable = {
                    StatusChip(status = patientWithCatalogs.patient.status)
                }
            )
        }
    }
}

/**
 * Composable auxiliar responsivo para las filas del Card
 */
@Composable
private fun DetailRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    valueIcon: ImageVector? = null,
    colorValue: Color = MaterialTheme.colorScheme.onSurface,
    colorIconValue: Color = MaterialTheme.colorScheme.primary,
    valueComposable: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Lado Izquierdo: Icono + Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Lado Derecho: Valor + Icono opcional (ej: Check)
        if (valueComposable != null){
            valueComposable()
        }else{
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1.2f)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorValue,
                    textAlign = TextAlign.End
                )
                if (valueIcon != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = valueIcon,
                        contentDescription = null,
                        tint = colorIconValue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}