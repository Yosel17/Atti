package yosel.dev.atti.screens.top_level.clients.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
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
import yosel.dev.atti.R
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.customColors

private enum class ClientsUIStatus {
    LOADING, CONTENT, EMPTY
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyClients(
    modifier: Modifier = Modifier,
    state: ClientsState,
    onAction: (ClientsAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val listState = rememberLazyListState()
    val uiStatus = when {
        state.isLoading -> ClientsUIStatus.LOADING
        state.clients.isNotEmpty() -> ClientsUIStatus.CONTENT
        else -> ClientsUIStatus.EMPTY
    }

    AnimatedContent(
        targetState = uiStatus,
        label = "ClientsContentTransition",
        modifier = modifier.fillMaxSize()
    ) { status ->
        when (status) {
            ClientsUIStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            ClientsUIStatus.EMPTY -> {
                EmptyClientsState(onAddClientClick = { onNavigationMain(Screens.AddClient) })
            }
            ClientsUIStatus.CONTENT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    AttiSearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(ClientsAction.OnSearchQueryChange(it)) },
                        placeholder = "Buscar clientes...",
                        onFilterClick = { onAction(ClientsAction.OnToggleFilterSheet(true)) }
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
                        label = "ClientsSearchTransition"
                    ) { isSearchEmpty ->
                        if (isSearchEmpty) {
                            NoSearchResultsState(
                                query = state.searchQuery,
                                onClearSearch = { onAction(ClientsAction.OnSearchQueryChange("")) },
                                nameResult = "clientes"
                            )
                        } else {
                            ClientList(
                                modifier = Modifier.fillMaxSize(),
                                clients = state.filteredClients,
                                listState = listState,
                                onAction = onAction,
                                onClientClick = { clientId ->
                                    onNavigationMain(
                                        Screens.DetailClient(
                                            clientId = clientId,
                                            isLocalPatients = true
                                        )
                                    )
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
fun ClientList(
    clients: List<ClientModel>,
    listState: LazyListState,
    onAction: (ClientsAction) -> Unit,
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
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(clients, key = { it.id }) { client ->
            ClientItem(
                modifier = Modifier.animateItem(),
                client = client,
                onCallClick = { onAction(ClientsAction.OnCallClick(it)) },
                onMessageClick = { onAction(ClientsAction.OnWhatsappClick(it)) },
                onClientClick = onClientClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${client.firstName} ${client.lastName}".trim().ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
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
                    if (client.status == Constants.DELETED_CLIENT_STATUS) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
        Button(onClick = onAddClientClick) {
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