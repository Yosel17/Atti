package yosel.dev.atti.screens.top_level.receipts.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel
import yosel.dev.atti.core.models.model.ReceiptItemModel
import yosel.dev.atti.core.models.model.ReceiptItemWithDetailsModel
import yosel.dev.atti.core.models.model.ReceiptModel
import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.formatDate
import yosel.dev.atti.core.utils.formatShortDate
import yosel.dev.atti.core.utils.getIconSpecies
import yosel.dev.atti.ui.theme.AttiTheme
import java.util.Locale

private enum class ReceiptsUIStatus { LOADING, CONTENT, EMPTY }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyReceipts(
    modifier: Modifier = Modifier,
    state: ReceiptsState,
    onAction: (ReceiptsAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val listState = rememberLazyListState()
    val uiStatus = when {
        state.isLoading -> ReceiptsUIStatus.LOADING
        state.receipts.isNotEmpty() -> ReceiptsUIStatus.CONTENT
        else -> ReceiptsUIStatus.EMPTY
    }

    AnimatedContent(
        targetState = uiStatus,
        label = "ReceiptsContentTransition",
        modifier = modifier.fillMaxSize()
    ) { status ->
        when (status) {
            ReceiptsUIStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            ReceiptsUIStatus.EMPTY -> {
                EmptyReceiptsState(onAddReceiptClick = { onNavigationMain(Screens.ReceiptForm()) })
            }
            ReceiptsUIStatus.CONTENT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    AttiSearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(ReceiptsAction.OnSearchQueryChange(it)) },
                        placeholder = "Buscar por cliente, paciente o número...",
                        onFilterClick = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CountBadge(
                        modifier = Modifier.fillMaxWidth(),
                        count = state.filteredReceipts.size,
                        title = "Total de recibos"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = state.filteredReceipts.isEmpty(),
                        label = "ReceiptsSearchTransition"
                    ) { isSearchEmpty ->
                        if (isSearchEmpty) {
                            NoSearchResultsState(
                                query = state.searchQuery,
                                onClearSearch = { onAction(ReceiptsAction.OnSearchQueryChange("")) },
                                nameResult = "recibos"
                            )
                        } else {
                            ReceiptList(
                                modifier = Modifier.fillMaxSize(),
                                receipts = state.filteredReceipts,
                                listState = listState,
                                onItemClick = { receiptId, consultationId ->
                                    onNavigationMain(
                                        Screens.ReceiptForm(
                                            consultationId = consultationId,
                                            receiptId = receiptId
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
fun ReceiptList(
    modifier: Modifier = Modifier,
    receipts: List<ReceiptWithDetailsModel>,
    listState: LazyListState,
    onItemClick: (receiptId: String, consultationId: String?) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(receipts.size) }
    val firstReceiptId = receipts.firstOrNull()?.receipt?.id

    LaunchedEffect(receipts.size, firstReceiptId) {
        if (receipts.size > previousCount && receipts.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = receipts.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(receipts, key = { it.receipt.id }) { receiptItem ->
            ReceiptCard(
                modifier = Modifier.animateItem(),
                receiptWithDetails = receiptItem,
                onClick = {
                    onItemClick(receiptItem.receipt.id, receiptItem.receipt.consultationId)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
fun ReceiptCard(
    modifier: Modifier = Modifier,
    receiptWithDetails: ReceiptWithDetailsModel,
    onClick: () -> Unit
) {
    val receipt = receiptWithDetails.receipt
    val patient = receiptWithDetails.consultationWithDetails?.patientWithDetails?.patient
    val client = receiptWithDetails.consultationWithDetails?.patientWithDetails?.client

    val displayName = remember(receipt.customerName, client) {
        when {
            receipt.customerName.isNotBlank() -> receipt.customerName
            client != null && (client.firstName.isNotBlank() || client.lastName.isNotBlank()) ->
                "${client.firstName} ${client.lastName}".trim()
            else -> "Cliente General"
        }
    }

    val dateFormatted = remember(receipt.createdAt) {
        formatShortDate(receipt.createdAt)
    }

    val formattedTotal = remember(receipt.total) {
        "Q%.2f".format(Locale.US, receipt.total)
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado del Recibo: Número y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Recibo #${receipt.receiptNumber.toString().padStart(4, '0')}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información de Cliente y Paciente
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (patient != null && patient.name.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val speciesIcon = getIconSpecies(patient.speciesId)
                            Icon(
                                painter = painterResource(id = speciesIcon),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Paciente: ${patient.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (receipt.status == Constants.DELETED_STATUS) {
                Spacer(modifier = Modifier.height(8.dp))
                StatusChipShort(
                    modifier = Modifier.align(Alignment.End),
                    status = receipt.status
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Resumen de Ítems y Monto Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    val itemsCount = receiptWithDetails.items.size
                    Text(
                        text = "$itemsCount ${if (itemsCount == 1) "ítem" else "ítems"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Cobrado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formattedTotal,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyReceiptsState(
    modifier: Modifier = Modifier,
    onAddReceiptClick: () -> Unit
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
                imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin recibos registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes recibos o cobros registrados en la veterinaria.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onAddReceiptClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Crear primer recibo")
        }
    }
}

@PreviewLightDark
@Composable
private fun BodyReceiptsPreview() {
    val sampleReceipts = listOf(
        ReceiptWithDetailsModel(
            receipt = ReceiptModel(
                id = "1",
                receiptNumber = 101L,
                customerName = "Juan Pérez aj kldsf asdklf asdlkf jasldkf jaslkdf jasldf asldkf asldkf aslkd ",
                total = 250.00,
                createdAt = "2025-01-15T10:30:00Z"
            ),
            consultationWithDetails = ConsultationWithDetailsModel(
                patientWithDetails = PatientWithDetailsModel(
                    patient = PatientModel(name = "Max", speciesId = 1),
                    client = ClientModel(firstName = "Juan", lastName = "Pérez")
                )
            ),
            items = listOf(
                ReceiptItemWithDetailsModel(item = ReceiptItemModel(id = "item1")),
                ReceiptItemWithDetailsModel(item = ReceiptItemModel(id = "item2"))
            )
        ),
        ReceiptWithDetailsModel(
            receipt = ReceiptModel(
                id = "2",
                receiptNumber = 102L,
                customerName = "María López",
                total = 120.50,
                createdAt = "2025-01-16T14:15:00Z"
            ),
            consultationWithDetails = ConsultationWithDetailsModel(
                patientWithDetails = PatientWithDetailsModel(
                    patient = PatientModel(name = "Luna", speciesId = 2),
                    client = ClientModel(firstName = "María", lastName = "López")
                )
            ),
            items = listOf(
                ReceiptItemWithDetailsModel(item = ReceiptItemModel(id = "item3"))
            )
        ),
        ReceiptWithDetailsModel(
            receipt = ReceiptModel(
                id = "3",
                receiptNumber = 103L,
                customerName = "Carlos Gómez",
                total = 500.00,
                createdAt = "2025-01-17T09:00:00Z",
                status = Constants.DELETED_STATUS
            ),
            consultationWithDetails = ConsultationWithDetailsModel(
                patientWithDetails = PatientWithDetailsModel(
                    patient = PatientModel(name = "Rocky", speciesId = 1),
                    client = ClientModel(firstName = "Carlos", lastName = "Gómez")
                )
            ),
            items = listOf(
                ReceiptItemWithDetailsModel(item = ReceiptItemModel(id = "item4")),
                ReceiptItemWithDetailsModel(item = ReceiptItemModel(id = "item5")),
                ReceiptItemWithDetailsModel(item = ReceiptItemModel(id = "item6"))
            )
        )
    )

    val sampleState = ReceiptsState(
        isLoading = false,
        receipts = sampleReceipts,
        filteredReceipts = sampleReceipts,
        searchQuery = ""
    )

    AttiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BodyReceipts(
                state = sampleState,
                onAction = {},
                onNavigationMain = {}
            )
        }
    }
}