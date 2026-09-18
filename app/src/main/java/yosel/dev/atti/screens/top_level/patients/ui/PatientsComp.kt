package yosel.dev.atti.screens.top_level.patients.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.PatientWithDetailsModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.getIconGender
import yosel.dev.atti.core.utils.getIconSpecies

private enum class PatientsUIStatus {
    LOADING, CONTENT, EMPTY
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyPatients(
    modifier: Modifier = Modifier,
    state: PatientsState,
    onAction: (PatientsAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val listState = rememberLazyListState()
    val uiStatus = when {
        state.isLoading -> PatientsUIStatus.LOADING
        state.patients.isNotEmpty() -> PatientsUIStatus.CONTENT
        else -> PatientsUIStatus.EMPTY
    }

    AnimatedContent(
        targetState = uiStatus,
        label = "PatientsContentTransition",
        modifier = modifier.fillMaxSize()
    ) { status ->
        when (status) {
            PatientsUIStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            PatientsUIStatus.EMPTY -> {
                NotFoundPatientsState(onAddPatientClick = { onNavigationMain(Screens.AddPatient()) })
            }
            PatientsUIStatus.CONTENT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    AttiSearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(PatientsAction.OnSearchQueryChange(it)) },
                        placeholder = "Buscar pacientes...",
                        onFilterClick = { onAction(PatientsAction.OnToggleFilterSheet(true)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CountBadge(
                        modifier = Modifier.fillMaxWidth(),
                        count = state.filteredPatients.size,
                        title = "Total de pacientes"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = state.filteredPatients.isEmpty(),
                        label = "PatientsSearchTransition"
                    ) { isSearchEmpty ->
                        if (isSearchEmpty) {
                            NoSearchResultsState(
                                query = state.searchQuery,
                                onClearSearch = { onAction(PatientsAction.OnSearchQueryChange("")) },
                                nameResult = "pacientes"
                            )
                        } else {
                            PatientList(
                                modifier = Modifier.fillMaxSize(),
                                patients = state.filteredPatients,
                                listState = listState,
                                onPatientClick = { patientId ->
                                    onNavigationMain(Screens.DetailPatient(patientId = patientId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientList(
    patients: List<PatientWithDetailsModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onPatientClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(patients.size) }
    val firstPatientId = patients.firstOrNull()?.patient?.id
    LaunchedEffect(patients.size, firstPatientId) {
        if (patients.size > previousCount && patients.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = patients.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(patients, key = { it.patient.id }) { patientItem ->
            PatientCard(
                modifier = Modifier.animateItem(),
                patientWithCatalogs = patientItem,
                onCardClick = { onPatientClick(patientItem.patient.id) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
fun PatientCard(
    patientWithCatalogs: PatientWithDetailsModel,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSpecie = getIconSpecies(patientWithCatalogs.patient.speciesId)
    val iconGender = getIconGender(patientWithCatalogs.patient.genderId)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconSpecie),
                        contentDescription = patientWithCatalogs.species.name,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patientWithCatalogs.patient.name.ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (patientWithCatalogs.patient.breed.isNotBlank()) "Raza: ${patientWithCatalogs.patient.breed}" else "Raza no especificada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.align(Alignment.Top),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = patientWithCatalogs.species.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Cake,
                        contentDescription = "Edad",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = patientWithCatalogs.patient.formattedAge,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = iconGender,
                        contentDescription = "Género",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = patientWithCatalogs.gender.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ContentCut,
                    contentDescription = "Castrado",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Castrado: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (patientWithCatalogs.patient.isNeutered) "Sí" else "No",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (patientWithCatalogs.patient.isNeutered) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }
            if (patientWithCatalogs.patient.status == Constants.DELETED_PATIENT_STATUS) {
                Spacer(modifier = Modifier.height(8.dp))
                StatusChipShort(
                    modifier = Modifier.align(Alignment.End),
                    status = patientWithCatalogs.patient.status
                )
            }
        }
    }
}

@Composable
fun NotFoundPatientsState(
    onAddPatientClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Pets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin pacientes registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes pacientes registrados. Agrega el primero para comenzar con el flujo.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onAddPatientClick) {
            Icon(
                imageVector = Icons.Outlined.Pets,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer paciente")
        }
    }
}