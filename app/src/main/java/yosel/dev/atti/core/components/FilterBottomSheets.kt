package yosel.dev.atti.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.filter.ClientFilter
import yosel.dev.atti.core.models.filter.DateSortOrder
import yosel.dev.atti.core.models.filter.NeuteredFilter
import yosel.dev.atti.core.models.filter.PatientFilter
import yosel.dev.atti.core.models.filter.ProductFilter
import yosel.dev.atti.core.models.filter.ServiceFilter
import yosel.dev.atti.core.models.filter.StatusFilter
import yosel.dev.atti.core.models.filter.SupplierFilter
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.SupplierModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModalContainer(
    title: String,
    onDismissRequest: () -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    val closeWithAnimation = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { closeWithAnimation() }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cerrar filtros")
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onReset) {
                    Text("Restablecer")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onApply()
                    closeWithAnimation()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Aplicar filtros", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> FilterChipGroup(
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            FilterChip(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                label = { Text(itemLabel(item)) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientFilterBottomSheet(
    initialFilter: ClientFilter,
    onDismissRequest: () -> Unit,
    onApply: (ClientFilter) -> Unit
) {
    var draft by remember { mutableStateOf(initialFilter) }

    FilterModalContainer(
        title = "Filtros de Clientes",
        onDismissRequest = onDismissRequest,
        onReset = { draft = ClientFilter() },
        onApply = { onApply(draft) }
    ) {
        FilterSection("Estado") {
            FilterChipGroup(
                items = StatusFilter.entries,
                selectedItem = draft.status,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(status = it) }
            )
        }
        FilterSection("Fecha de registro") {
            FilterChipGroup(
                items = DateSortOrder.entries,
                selectedItem = draft.dateSort,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(dateSort = it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFilterBottomSheet(
    initialFilter: PatientFilter,
    availableSpecies: List<AppCatalogModel>,
    availableGenders: List<AppCatalogModel>,
    onDismissRequest: () -> Unit,
    onApply: (PatientFilter) -> Unit
) {
    var draft by remember { mutableStateOf(initialFilter) }

    FilterModalContainer(
        title = "Filtros de Pacientes",
        onDismissRequest = onDismissRequest,
        onReset = { draft = PatientFilter() },
        onApply = { onApply(draft) }
    ) {
        if (availableSpecies.isNotEmpty()) {
            FilterSection("Especie") {
                val speciesOptions = listOf<AppCatalogModel?>(null) + availableSpecies
                FilterChipGroup(
                    items = speciesOptions,
                    selectedItem = availableSpecies.find { it.id == draft.speciesId },
                    itemLabel = { it?.name ?: "Todas" },
                    onItemSelected = { draft = draft.copy(speciesId = it?.id) }
                )
            }
        }
        if (availableGenders.isNotEmpty()) {
            FilterSection("Género") {
                val genderOptions = listOf<AppCatalogModel?>(null) + availableGenders
                FilterChipGroup(
                    items = genderOptions,
                    selectedItem = availableGenders.find { it.id == draft.genderId },
                    itemLabel = { it?.name ?: "Todos" },
                    onItemSelected = { draft = draft.copy(genderId = it?.id) }
                )
            }
        }
        FilterSection("Castrado / Esterilizado") {
            FilterChipGroup(
                items = NeuteredFilter.entries,
                selectedItem = draft.neutered,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(neutered = it) }
            )
        }
        FilterSection("Estado") {
            FilterChipGroup(
                items = StatusFilter.entries,
                selectedItem = draft.status,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(status = it) }
            )
        }
        FilterSection("Fecha de registro") {
            FilterChipGroup(
                items = DateSortOrder.entries,
                selectedItem = draft.dateSort,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(dateSort = it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFilterBottomSheet(
    initialFilter: ProductFilter,
    categories: List<AppCatalogModel>,
    unitTypes: List<AppCatalogModel>,
    suppliers: List<SupplierModel>,
    onDismissRequest: () -> Unit,
    onApply: (ProductFilter) -> Unit
) {
    var draft by remember { mutableStateOf(initialFilter) }

    FilterModalContainer(
        title = "Filtros de Productos",
        onDismissRequest = onDismissRequest,
        onReset = { draft = ProductFilter() },
        onApply = { onApply(draft) }
    ) {
        if (categories.isNotEmpty()) {
            FilterSection("Categoría") {
                val opts = listOf<AppCatalogModel?>(null) + categories
                FilterChipGroup(
                    items = opts,
                    selectedItem = categories.find { it.id == draft.categoryId },
                    itemLabel = { it?.name ?: "Todas" },
                    onItemSelected = { draft = draft.copy(categoryId = it?.id) }
                )
            }
        }
        if (unitTypes.isNotEmpty()) {
            FilterSection("Unidad de medida") {
                val opts = listOf<AppCatalogModel?>(null) + unitTypes
                FilterChipGroup(
                    items = opts,
                    selectedItem = unitTypes.find { it.id == draft.unitTypeId },
                    itemLabel = { it?.name ?: "Todas" },
                    onItemSelected = { draft = draft.copy(unitTypeId = it?.id) }
                )
            }
        }
        if (suppliers.isNotEmpty()) {
            FilterSection("Proveedor") {
                val opts = listOf<SupplierModel?>(null) + suppliers
                FilterChipGroup(
                    items = opts,
                    selectedItem = suppliers.find { it.id == draft.supplierId },
                    itemLabel = { it?.name ?: "Todos" },
                    onItemSelected = { draft = draft.copy(supplierId = it?.id) }
                )
            }
        }
        FilterSection("Estado") {
            FilterChipGroup(
                items = StatusFilter.entries,
                selectedItem = draft.status,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(status = it) }
            )
        }
        FilterSection("Fecha de registro") {
            FilterChipGroup(
                items = DateSortOrder.entries,
                selectedItem = draft.dateSort,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(dateSort = it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFilterBottomSheet(
    initialFilter: ServiceFilter,
    categories: List<AppCatalogModel>,
    onDismissRequest: () -> Unit,
    onApply: (ServiceFilter) -> Unit
) {
    var draft by remember { mutableStateOf(initialFilter) }

    FilterModalContainer(
        title = "Filtros de Servicios",
        onDismissRequest = onDismissRequest,
        onReset = { draft = ServiceFilter() },
        onApply = { onApply(draft) }
    ) {
        if (categories.isNotEmpty()) {
            FilterSection("Categoría") {
                val opts = listOf<AppCatalogModel?>(null) + categories
                FilterChipGroup(
                    items = opts,
                    selectedItem = categories.find { it.id == draft.categoryId },
                    itemLabel = { it?.name ?: "Todas" },
                    onItemSelected = { draft = draft.copy(categoryId = it?.id) }
                )
            }
        }
        FilterSection("Estado") {
            FilterChipGroup(
                items = StatusFilter.entries,
                selectedItem = draft.status,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(status = it) }
            )
        }
        FilterSection("Fecha de registro") {
            FilterChipGroup(
                items = DateSortOrder.entries,
                selectedItem = draft.dateSort,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(dateSort = it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierFilterBottomSheet(
    initialFilter: SupplierFilter,
    onDismissRequest: () -> Unit,
    onApply: (SupplierFilter) -> Unit
) {
    var draft by remember { mutableStateOf(initialFilter) }

    FilterModalContainer(
        title = "Filtros de Proveedores",
        onDismissRequest = onDismissRequest,
        onReset = { draft = SupplierFilter() },
        onApply = { onApply(draft) }
    ) {
        FilterSection("Estado") {
            FilterChipGroup(
                items = StatusFilter.entries,
                selectedItem = draft.status,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(status = it) }
            )
        }
        FilterSection("Fecha de registro") {
            FilterChipGroup(
                items = DateSortOrder.entries,
                selectedItem = draft.dateSort,
                itemLabel = { it.label },
                onItemSelected = { draft = draft.copy(dateSort = it) }
            )
        }
    }
}