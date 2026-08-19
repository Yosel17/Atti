package yosel.dev.atti.screens.navigation_bar.directory.ui

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Pets
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import yosel.dev.atti.R
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.getIconGender
import yosel.dev.atti.core.utils.getIconSpecies
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
    onNavigationMain: (Screens) -> Unit,
    onAction: (DirectoryAction) -> Unit
) {
    val tabs = remember {
        listOf(
            DirectoryTabData("Clientes", Icons.Filled.People),
            DirectoryTabData("Pacientes", Icons.Filled.Pets)
        )
    }

    val clientListState = rememberLazyListState()
    val patientListState = rememberLazyListState()

    Column(modifier = modifier) {
        SecondaryTabRow(
            selectedTabIndex = state.selectedTabIndex,
            divider = {}
        ) {
            tabs.forEachIndexed { index, tabData ->
                Tab(
                    selected = state.selectedTabIndex == index,
                    onClick = { onAction(DirectoryAction.OnTabSelected(index)) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground,
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
                        state.isLoadingClients -> DirectoryUIStatus.LOADING
                        state.clients.isNotEmpty() -> DirectoryUIStatus.CONTENT
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
                                    CountBadge(
                                        modifier = Modifier.fillMaxWidth(),
                                        count = state.filteredClients.size,
                                        title = "Total de clientes"
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

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
                                                },
                                                nameResult = "clientes"
                                            )
                                        } else {
                                            ClientList(
                                                modifier = Modifier.fillMaxSize(),
                                                clients = state.filteredClients,
                                                listState = clientListState,
                                                onAction = onAction,
                                                onClientClick = { clientId ->
                                                    onNavigationMain(
                                                        Screens.DetailClient(
                                                            clientId = clientId,
                                                            isLocalPatients = !state.isFirstPatients
                                                        )
                                                    )
                                                }
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
                        state.patientsWithCatalogs.isNotEmpty() -> DirectoryUIStatus.CONTENT
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
                                    CountBadge(
                                        modifier = Modifier.fillMaxWidth(),
                                        count = state.filteredPatientsWithCatalogs.size,
                                        title = "Total de pacientes"
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (state.filteredPatientsWithCatalogs.isEmpty()) {
                                        NoSearchResultsState(
                                            query = state.patientSearchQuery,
                                            onClearSearch = {
                                                onAction(
                                                    DirectoryAction.OnPatientSearchQueryChange(
                                                        ""
                                                    )
                                                )
                                            },
                                            nameResult = "Pacientes"
                                        )
                                    } else {
                                        PatientList(
                                            modifier = Modifier.fillMaxSize(),
                                            patientsWithCatalogs = state.filteredPatientsWithCatalogs,
                                            listState = patientListState,
                                            onPatientClick = { patientId ->
                                                onNavigationMain(Screens.DetailPatient(patientId = patientId))
                                            }
                                        )
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
    listState: LazyListState,
    onAction: (DirectoryAction) -> Unit,
    modifier: Modifier = Modifier,
    onClientClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(clients.size) }
    val firstClientId = clients.firstOrNull()?.id

    LaunchedEffect(clients.size, firstClientId) {
        if (clients.size > previousCount && clients.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = clients.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(clients, key = { it.id }) { client ->
            ClientItem(
                modifier = Modifier.animateItem(),
                client = client,
                onCallClick = { onAction(DirectoryAction.OnCallClick(it)) },
                onMessageClick = { onAction(DirectoryAction.OnWhatsappClick(it)) },
                onClientClick = { clientId ->
                    onClientClick(clientId)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ClientItem(
    client: ClientModel,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit,
    onClientClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClientClick(client.id) },
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
                    if (client.status == Constants.DELETED_CLIENT_STATUS){

                        Spacer(modifier = Modifier.height(8.dp))

                        StatusChipShort(
                            modifier = Modifier.align(Alignment.End),
                            status = client.status
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
fun PatientList(
    patientsWithCatalogs: List<PatientWithCatalogsModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onPatientClick: (String) -> Unit
) {
    // 💡 REGLA 1: Detectar adición de un nuevo paciente para hacer auto-scroll
    var previousCount by remember { mutableIntStateOf(patientsWithCatalogs.size) }
    val firstPatientId = patientsWithCatalogs.firstOrNull()?.patient?.id

    LaunchedEffect(patientsWithCatalogs.size, firstPatientId) {
        if (patientsWithCatalogs.size > previousCount && patientsWithCatalogs.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = patientsWithCatalogs.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(patientsWithCatalogs, key = { it.patient.id }) { patientWithCatalogs ->
            PatientCard(
                modifier = Modifier.animateItem(),
                patientWithCatalogs = patientWithCatalogs,
                onCardClick = { onPatientClick(patientWithCatalogs.patient.id) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
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
fun PatientCard(
    patientWithCatalogs: PatientWithCatalogsModel,
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
                        painter = painterResource(iconSpecie),
                        contentDescription = patientWithCatalogs.species.name,
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

                // Badge / Chip de Especie
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
                        text = patientWithCatalogs.patient.formattedAge,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Género
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
                    text = if (patientWithCatalogs.patient.isNeutered) "Sí" else "No",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if(patientWithCatalogs.patient.isNeutered) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }

            if (patientWithCatalogs.patient.status == Constants.DELETED_PATIENT_STATUS){
                Spacer(modifier = Modifier.height(8.dp))

                StatusChipShort(
                    modifier = Modifier.align(Alignment.End),
                    status = patientWithCatalogs.patient.status
                )

            }
        }
    }
}