package yosel.dev.atti.screens.top_level.services.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import java.text.NumberFormat
import java.util.Locale

private enum class ServicesUIStatus { LOADING, CONTENT, EMPTY }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyServices(
    modifier: Modifier = Modifier,
    state: ServicesState,
    onAction: (ServicesAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val listState = rememberLazyListState()
    val uiStatus = when {
        state.isLoading -> ServicesUIStatus.LOADING
        state.services.isNotEmpty() -> ServicesUIStatus.CONTENT
        else -> ServicesUIStatus.EMPTY
    }

    AnimatedContent(
        targetState = uiStatus,
        label = "ServicesContentTransition",
        modifier = modifier.fillMaxSize()
    ) { status ->
        when (status) {
            ServicesUIStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            ServicesUIStatus.EMPTY -> {
                EmptyServicesState(onAddServiceClick = { onNavigationMain(Screens.ServiceForm()) })
            }
            ServicesUIStatus.CONTENT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    AttiSearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(ServicesAction.OnSearchQueryChange(it)) },
                        placeholder = "Buscar servicios...",
                        onFilterClick = { onAction(ServicesAction.OnToggleFilterSheet(true)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CountBadge(
                        modifier = Modifier.fillMaxWidth(),
                        count = state.filteredServices.size,
                        title = "Total de servicios"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = state.filteredServices.isEmpty(),
                        label = "ServicesSearchTransition"
                    ) { isSearchEmpty ->
                        if (isSearchEmpty) {
                            NoSearchResultsState(
                                query = state.searchQuery,
                                onClearSearch = { onAction(ServicesAction.OnSearchQueryChange("")) },
                                nameResult = "servicios"
                            )
                        } else {
                            ServiceList(
                                modifier = Modifier.fillMaxSize(),
                                services = state.filteredServices,
                                listState = listState,
                                onClick = { serviceId ->
                                    onNavigationMain(Screens.DetailService(serviceId = serviceId))
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
fun ServiceList(
    services: List<ServiceWithDetailsModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(services.size) }
    val firstServiceId = services.firstOrNull()?.service?.id

    LaunchedEffect(services.size, firstServiceId) {
        if (services.size > previousCount && services.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = services.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(services, key = { it.service.id }) { serviceWithDetails ->
            ServiceItemCard(
                modifier = Modifier.animateItem(),
                item = serviceWithDetails,
                onClickItem = onClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
fun ServiceItemCard(
    item: ServiceWithDetailsModel,
    modifier: Modifier = Modifier,
    onClickItem: (String) -> Unit
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-GT")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }
    val formattedSalePrice = remember(item.service.salePrice) {
        currencyFormatter.format(item.service.salePrice)
    }
    val formattedEstimatedCost = remember(item.service.estimatedCost) {
        currencyFormatter.format(item.service.estimatedCost)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { onClickItem(item.service.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.service.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Categoría: ${item.category.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (item.service.status == Constants.DELETED_STATUS) {
                Spacer(modifier = Modifier.height(4.dp))
                StatusChipShort(
                    modifier = Modifier.align(Alignment.End),
                    status = item.service.status
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Precio de venta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedSalePrice,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Gastos de insumos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedEstimatedCost,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyServicesState(
    modifier: Modifier = Modifier,
    onAddServiceClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin servicios registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes servicios registrados en el sistema.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onAddServiceClick) {
            Icon(imageVector = Icons.Outlined.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer servicio")
        }
    }
}