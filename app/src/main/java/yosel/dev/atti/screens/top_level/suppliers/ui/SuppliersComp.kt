package yosel.dev.atti.screens.top_level.suppliers.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.customColors

private enum class SuppliersUIStatus { LOADING, CONTENT, EMPTY }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodySuppliers(
    modifier: Modifier = Modifier,
    state: SuppliersState,
    onAction: (SuppliersAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val listState = rememberLazyListState()
    val uiStatus = when {
        state.isLoading -> SuppliersUIStatus.LOADING
        state.suppliers.isNotEmpty() -> SuppliersUIStatus.CONTENT
        else -> SuppliersUIStatus.EMPTY
    }

    AnimatedContent(
        targetState = uiStatus,
        label = "SuppliersContentTransition",
        modifier = modifier.fillMaxSize()
    ) { status ->
        when (status) {
            SuppliersUIStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            SuppliersUIStatus.EMPTY -> {
                EmptySuppliersState(onAddSupplierClick = { onNavigationMain(Screens.AddSupplier) })
            }
            SuppliersUIStatus.CONTENT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    AttiSearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(SuppliersAction.OnSearchQueryChange(it)) },
                        placeholder = "Buscar proveedores...",
                        onFilterClick = { onAction(SuppliersAction.OnToggleFilterSheet(true)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CountBadge(
                        modifier = Modifier.fillMaxWidth(),
                        count = state.filteredSuppliers.size,
                        title = "Total de proveedores"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = state.filteredSuppliers.isEmpty(),
                        label = "SuppliersSearchTransition"
                    ) { isSearchEmpty ->
                        if (isSearchEmpty) {
                            NoSearchResultsState(
                                query = state.searchQuery,
                                onClearSearch = { onAction(SuppliersAction.OnSearchQueryChange("")) },
                                nameResult = "proveedores"
                            )
                        } else {
                            SupplierList(
                                modifier = Modifier.fillMaxSize(),
                                suppliers = state.filteredSuppliers,
                                listState = listState,
                                onAction = onAction,
                                onItemClick = { supplierId ->
                                    onNavigationMain(Screens.DetailSupplier(supplierId = supplierId))
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
fun SupplierList(
    modifier: Modifier = Modifier,
    suppliers: List<SupplierModel>,
    listState: LazyListState,
    onAction: (SuppliersAction) -> Unit,
    onItemClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(suppliers.size) }
    val firstSupplierId = suppliers.firstOrNull()?.id

    LaunchedEffect(suppliers.size, firstSupplierId) {
        if (suppliers.size > previousCount && suppliers.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = suppliers.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(suppliers, key = { it.id }) { supplier ->
            SupplierItem(
                modifier = Modifier.animateItem(),
                supplier = supplier,
                onCallClick = { onAction(SuppliersAction.OnCallClick(it)) },
                onWhatsAppClick = { onAction(SuppliersAction.OnWhatsappClick(it)) },
                onItemClick = onItemClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
fun SupplierItem(
    supplier: SupplierModel,
    onCallClick: (String) -> Unit,
    onWhatsAppClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onItemClick(supplier.id) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = supplier.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    SupplierInfoRow(icon = Icons.Outlined.Phone, text = supplier.phoneNumber)
                    if (supplier.taxId.isNotBlank()) {
                        SupplierInfoRow(icon = Icons.Outlined.Badge, text = "NIT: ${supplier.taxId}")
                    }
                    if (supplier.address.isNotBlank()) {
                        SupplierInfoRow(icon = Icons.Outlined.LocationOn, text = supplier.address)
                    }
                    if (supplier.status == Constants.DELETED_STATUS) {
                        Spacer(modifier = Modifier.height(8.dp))
                        StatusChipShort(modifier = Modifier.align(Alignment.End), status = supplier.status)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCallClick(supplier.phoneNumber) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = "Llamar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Llamar",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onWhatsAppClick(supplier.phoneNumber) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.whatsapp),
                        contentDescription = "WhatsApp",
                        tint = MaterialTheme.customColors.whatsapp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WhatsApp",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.customColors.whatsapp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SupplierInfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun EmptySuppliersState(
    modifier: Modifier = Modifier,
    onAddSupplierClick: () -> Unit
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
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin proveedores registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes proveedores registrados en el sistema.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onAddSupplierClick) {
            Icon(imageVector = Icons.Outlined.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer proveedor")
        }
    }
}