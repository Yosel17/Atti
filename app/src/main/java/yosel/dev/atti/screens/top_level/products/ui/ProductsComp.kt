package yosel.dev.atti.screens.top_level.products.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.WarningAmber
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AttiSearchBar
import yosel.dev.atti.core.components.CountBadge
import yosel.dev.atti.core.components.NoSearchResultsState
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.CustomColors
import yosel.dev.atti.ui.theme.customColors
import java.util.Locale

private enum class ProductsUIStatus { LOADING, CONTENT, EMPTY }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BodyProducts(
    modifier: Modifier = Modifier,
    state: ProductsState,
    onAction: (ProductsAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {
    val listState = rememberLazyListState()
    val uiStatus = when {
        state.isLoading -> ProductsUIStatus.LOADING
        state.products.isNotEmpty() -> ProductsUIStatus.CONTENT
        else -> ProductsUIStatus.EMPTY
    }

    AnimatedContent(
        targetState = uiStatus,
        label = "ProductsContentTransition",
        modifier = modifier.fillMaxSize()
    ) { status ->
        when (status) {
            ProductsUIStatus.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            ProductsUIStatus.EMPTY -> {
                EmptyProductsState(onAddProductClick = { onNavigationMain(Screens.ProductForm()) })
            }
            ProductsUIStatus.CONTENT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    AttiSearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(ProductsAction.OnSearchQueryChange(it)) },
                        placeholder = "Buscar productos...",
                        onFilterClick = { onAction(ProductsAction.OnToggleFilterSheet(true)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CountBadge(
                        modifier = Modifier.fillMaxWidth(),
                        count = state.filteredProducts.size,
                        title = "Total de productos"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = state.filteredProducts.isEmpty(),
                        label = "ProductsSearchTransition"
                    ) { isSearchEmpty ->
                        if (isSearchEmpty) {
                            NoSearchResultsState(
                                query = state.searchQuery,
                                onClearSearch = { onAction(ProductsAction.OnSearchQueryChange("")) },
                                nameResult = "productos"
                            )
                        } else {
                            ProductList(
                                modifier = Modifier.fillMaxSize(),
                                products = state.filteredProducts,
                                listState = listState,
                                onItemClick = { productId ->
                                    onNavigationMain(Screens.DetailProduct(productId = productId))
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
fun ProductList(
    products: List<ProductWithDetailsModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    var previousCount by remember { mutableIntStateOf(products.size) }
    val firstProductId = products.firstOrNull()?.product?.id

    LaunchedEffect(products.size, firstProductId) {
        if (products.size > previousCount && products.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
        previousCount = products.size
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(products, key = { it.product.id }) { productWithDetails ->
            ProductCard(
                modifier = Modifier.animateItem(),
                productDetails = productWithDetails,
                onClick = onItemClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

enum class ProductStockStatus(val label: String, val icon: ImageVector) {
    OUT_OF_STOCK("Agotado", Icons.Rounded.Block),
    LOW_STOCK("Stock Bajo", Icons.Rounded.WarningAmber),
    IN_STOCK("En Stock", Icons.Rounded.CheckCircle);

    companion object {
        fun fromStock(stock: Int, minStock: Int): ProductStockStatus {
            return when {
                stock <= 0 -> OUT_OF_STOCK
                stock <= minStock -> LOW_STOCK
                else -> IN_STOCK
            }
        }
    }

    fun getColors(customColors: CustomColors): Pair<Color, Color> {
        return when (this) {
            OUT_OF_STOCK -> customColors.deleted to customColors.onDeleted
            LOW_STOCK -> customColors.inactiveContainer to customColors.onInactiveContainer
            IN_STOCK -> customColors.active to customColors.onActive
        }
    }
}

@Composable
fun ProductCard(
    productDetails: ProductWithDetailsModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val product = productDetails.product
    val unitType = productDetails.unitType
    val supplier = productDetails.supplier
    val customColors = MaterialTheme.customColors

    val stockStatus = remember(product.stock, product.minStock) {
        ProductStockStatus.fromStock(product.stock, product.minStock)
    }
    val (chipBgColor, chipContentColor) = stockStatus.getColors(customColors)

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(product.id) },
        shape = RoundedCornerShape(16.dp),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.commercialName.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                Surface(
                    shape = CircleShape,
                    color = chipBgColor,
                    contentColor = chipContentColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = stockStatus.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = stockStatus.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            if (unitType.name.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Text(
                        text = unitType.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Proveedor: ${supplier.name.ifBlank { "Sin nombre" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            if (product.status == Constants.DELETED_STATUS) {
                Spacer(modifier = Modifier.height(4.dp))
                StatusChipShort(
                    modifier = Modifier.align(Alignment.End),
                    status = product.status
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Precio de venta",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Q%.2f".format(Locale.US, product.salePrice),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val stockFormatted = if (product.stock % 1.0 == 0.0) {
                    product.stock.toInt().toString()
                } else {
                    "%.2f".format(Locale.US, product.stock)
                }

                Text(
                    text = "Stock: $stockFormatted",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (product.stock <= 0) customColors.deleted else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun EmptyProductsState(
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit
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
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sin productos registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aún no tienes productos registrados en tu inventario.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onAddProductClick) {
            Icon(imageVector = Icons.Outlined.Medication, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Agregar primer producto")
        }
    }
}