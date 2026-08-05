package yosel.dev.atti.screens.directory.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.R
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.utils.getSpeciesInfo
import yosel.dev.atti.ui.theme.AttiTheme
import yosel.dev.atti.ui.theme.customColors

private data class DirectoryTabData(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyDirectory(
    modifier: Modifier = Modifier,
    state: DirectoryState,
    onClientClick: (String) -> Unit,
    onAction: (DirectoryAction) -> Unit
) {
    val tabs = remember {
        listOf(
            DirectoryTabData("Clientes", Icons.Outlined.People),
            DirectoryTabData("Pacientes", Icons.Outlined.Pets)
        )
    }

    Column(modifier = modifier) {
        SecondaryTabRow(
            selectedTabIndex = state.selectedTabIndex,
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabData ->
                Tab(
                    selected = state.selectedTabIndex == index,
                    onClick = { onAction(DirectoryAction.OnTabSelected(index)) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = tabData.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = tabData.title,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                )
            }
        }

        AnimatedContent(
            targetState = state.selectedTabIndex,
            label = "TabContentTransition",
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { 30 }
                )).togetherWith(fadeOut(animationSpec = tween(150)))
            },
            modifier = Modifier.fillMaxSize()
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    val clientState = when {
                        state.clients.isNotEmpty() -> DirectoryUIStatus.CONTENT
                        state.isLoadingClients -> DirectoryUIStatus.LOADING
                        else -> DirectoryUIStatus.EMPTY
                    }

                    AnimatedContent(
                        targetState = clientState,
                        label = "MainContentAnimation",
                        modifier = Modifier.fillMaxSize()
                    ) { status ->
                        when (status) {
                            DirectoryUIStatus.CONTENT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 24.dp)
                                ) {
                                    AttiSearchBar(
                                        value = state.clientSearchQuery,
                                        onValueChange = { onAction(DirectoryAction.OnClientSearchQueryChange(it)) },
                                        placeholder = "Buscar clientes...",
                                        onFilterClick = { /* No acción por ahora */ },
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    AnimatedContent(
                                        targetState = state.filteredClients.isEmpty(),
                                        label = "SearchAnimation"
                                    ) { isSearchEmpty ->
                                        if (isSearchEmpty) {
                                            NoSearchResultsState(
                                                query = state.clientSearchQuery,
                                                onClearSearch = {
                                                    onAction(
                                                        DirectoryAction.OnClientSearchQueryChange(
                                                            ""
                                                        )
                                                    )
                                                }
                                            )
                                        } else {
                                            ClientList(
                                                modifier = Modifier.fillMaxSize(),
                                                clients = state.filteredClients,
                                                onAction = onAction,
                                            )
                                        }
                                    }
                                }
                            }

                            DirectoryUIStatus.LOADING -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator()
                                }
                            }

                            DirectoryUIStatus.EMPTY -> {
                                EmptyClientsState(
                                    onAddClientClick = {}
                                )
                            }
                        }
                    }
                }

                1 -> {
                    val patientStatus = when {
                        state.isLoadingPatients -> DirectoryUIStatus.LOADING
                        state.patients.isNotEmpty() -> DirectoryUIStatus.CONTENT
                        else -> DirectoryUIStatus.EMPTY
                    }

                    AnimatedContent(
                        targetState = patientStatus,
                        label = "PatientContentAnimation",
                        modifier = Modifier.fillMaxSize()
                    ) { status ->
                        when (status) {
                            DirectoryUIStatus.LOADING -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingIndicator()
                                }
                            }

                            DirectoryUIStatus.CONTENT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 24.dp)
                                ) {
                                    AttiSearchBar(
                                        value = state.patientSearchQuery,
                                        onValueChange = { onAction(DirectoryAction.OnPatientSearchQueryChange(it)) },
                                        placeholder = "Buscar pacientes...",
                                        onFilterClick = {}
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (state.filteredPatients.isEmpty()) {
                                        NoSearchResultsState(
                                            query = state.patientSearchQuery,
                                            onClearSearch = {
                                                onAction(
                                                    DirectoryAction.OnPatientSearchQueryChange(
                                                        ""
                                                    )
                                                )
                                            }
                                        )
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(vertical = 16.dp)
                                        ) {
                                            items(state.filteredPatients, key = { it.id }) { patient ->
                                                // Componente Card de Paciente
                                                PatientCard(
                                                    patient = patient,
                                                    onCardClick = {}
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            DirectoryUIStatus.EMPTY -> {
                                // Puedes usar un estado vacío genérico para Pacientes
                                NotFoundPatientsState(
                                    onAddPatientClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Representa los diferentes estados visuales de la pestaña de Clientes
 */
private enum class DirectoryUIStatus {
    LOADING,
    CONTENT,
    EMPTY
}

@Composable
fun ClientList(
    clients: List<ClientModel>,
    onAction: (DirectoryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(clients, key = { it.id }) { client ->
            ClientItem(
                client = client,
                onCallClick = { onAction(DirectoryAction.OnCallClick(it)) },
                onMessageClick = { onAction(DirectoryAction.OnWhatsappClick(it)) },
                onClientClick = {  }
            )
        }
    }
}

@Composable
fun ClientItem(
    client: ClientModel,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit,
    onClientClick: (ClientModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClientClick(client) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Avatar + Nombre y Detalles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar Circular
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Avatar de cliente",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Información del Cliente
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${client.firstName} ${client.lastName}".trim().ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Teléfono
                    if (client.phoneNumber.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Call,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = client.phoneNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // NIT / Documento de Identificación
                    val nitText = client.documentId.ifBlank { "CF" }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NIT: $nitText",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Acciones: Botón Llamar y Botón Mensaje
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Llamar
                FilledTonalButton(
                    onClick = { onCallClick(client.phoneNumber) },
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = "Llamar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Llamar", style = MaterialTheme.typography.labelLarge)
                }

                // Botón Mensaje
                FilledTonalButton(
                    onClick = { onMessageClick(client.phoneNumber) },
                    shape = CircleShape,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.customColors.whatsappContainer,
                        contentColor = MaterialTheme.customColors.onWhatsappContainer
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.whatsapp),
                        contentDescription = "Enviar mensaje",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mensaje", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun EmptyClientsState(
    onAddClientClick: () -> Unit,
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
                imageVector = Icons.Outlined.PersonAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin clientes registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes clientes registrados. Agrega el primero para comenzar con el flujo.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onAddClientClick
        ) {
            Icon(
                imageVector = Icons.Outlined.DocumentScanner,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer cliente")
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
        Button(
            onClick = onAddPatientClick
        ) {
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

@Composable
fun NoSearchResultsState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No encontramos clientes que coincidan con \"$query\". Prueba con otro nombre o limpia la búsqueda.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        FilledTonalButton(
            onClick = onClearSearch,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Limpiar búsqueda")
        }
    }
}

// Mapeo de género (1: Macho, 2: Hembra por convención estándar)
data class GenderInfo(
    val label: String,
    val icon: ImageVector
)

fun getGenderInfo(genderId: Int): GenderInfo {
    return when (genderId) {
        1 -> GenderInfo("Macho", Icons.Outlined.Male)
        2 -> GenderInfo("Hembra", Icons.Outlined.Female)
        else -> GenderInfo("Desconocido", Icons.Outlined.QuestionMark)
    }
}

@Composable
fun PatientCard(
    patient: PatientModel,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speciesInfo = getSpeciesInfo(patient.speciesId)
    val genderInfo = getGenderInfo(patient.genderId)

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
            // --- HEADER: Ícono + Nombre/Raza + Chip Especie ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono del contenedor superior izquierdo
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(speciesInfo.icon),
                        contentDescription = speciesInfo.label,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre y Raza (reemplazando al dueño)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = patient.name.ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (patient.breed.isNotBlank()) "Raza: ${patient.breed}" else "Raza no especificada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge / Chip de Especie
                Surface(
                    modifier = Modifier.align(Alignment.Top),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = speciesInfo.label,
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

            // --- DETALLES: Edad, Género y Esterilización ---
            // FlowRow asegura responsividad en pantallas estrechas sin cortar texto
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Cake,
                        contentDescription = "Edad",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = patient.formattedAge,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Género
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = genderInfo.icon,
                        contentDescription = "Género",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = genderInfo.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estado Castrado/Esterilizado
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
                    text = if (patient.isNeutered) "Sí" else "No",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if(patient.isNeutered) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // --- FOOTER: Ver Historia Clínica ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver historia clínica",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FactCheck,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ItemClientsPreview() {
    AttiTheme {
        PatientCard(
            patient = PatientModel(
                id = "1",
                clientId = "1",
                name = "Max",
                speciesId = 1,
                genderId = 1,
                breed = "Labrador",
                ageYears = 2,
                ageMonths = 1,
                color = "Blanco",
                isNeutered = true,
                photoUrl = "",
            ),
            onCardClick = {}
        )
    }
}