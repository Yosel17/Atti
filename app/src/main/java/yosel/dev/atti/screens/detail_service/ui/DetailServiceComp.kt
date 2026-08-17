package yosel.dev.atti.screens.detail_service.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import yosel.dev.atti.core.components.StatusChipShort
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel
import yosel.dev.atti.core.models.model.ServiceSupplyModel
import yosel.dev.atti.core.models.model.ServiceSupplyWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.ui.theme.AttiTheme
import java.util.Locale

@Composable
fun BodyDetailService(
    modifier: Modifier = Modifier,
    state: DetailServiceState
) {
    val serviceWithDetails = state.serviceWithDetails

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Cabecera (Nombre, Chip de estado y Categoría)
        ServiceHeaderSection(serviceWithDetails = serviceWithDetails)

        // 2. Card de Precios y Costos (Margen de ganancia y gastos)
        ServicePricesAndCostsCard(serviceWithDetails = serviceWithDetails)

        // 3. Sección de Insumos Vinculados
        ServiceSuppliesSection(serviceWithDetails = serviceWithDetails)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// 1. Cabecera
// ==========================================
@Composable
private fun ServiceHeaderSection(
    serviceWithDetails: ServiceWithDetailsModel,
    modifier: Modifier = Modifier
) {
    val service = serviceWithDetails.service
    val categoryName = serviceWithDetails.category.name.ifBlank { "Sin categoría" }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = service.name.ifBlank { "Sin nombre de servicio" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusChipShort(status = service.status)

            Text(
                text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// 2. Card de Precios y Costos
// ==========================================
@Composable
private fun ServicePricesAndCostsCard(
    serviceWithDetails: ServiceWithDetailsModel,
    modifier: Modifier = Modifier
) {
    val salePrice = serviceWithDetails.service.salePrice
    val estimatedCost = serviceWithDetails.service.estimatedCost

    val marginPercentage = if (salePrice > 0.0) {
        (((salePrice - estimatedCost) / salePrice) * 100).toInt().coerceAtLeast(0)
    } else {
        0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header del Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "Precios y Costos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Sub-card Precio de Venta y Margen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "PRECIO DE VENTA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Q ${salePrice.formatPrice()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 32.sp
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "MARGEN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$marginPercentage%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Fila Gastos de Insumos / Costo estimado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDownward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Gastos de Insumos",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Q ${estimatedCost.formatPrice()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ==========================================
// 3. Sección de Insumos Vinculados
// ==========================================
@Composable
private fun ServiceSuppliesSection(
    serviceWithDetails: ServiceWithDetailsModel,
    modifier: Modifier = Modifier
) {
    val supplies = serviceWithDetails.supplies

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título de la sección
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Widgets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Insumos Vinculados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (supplies.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = "Este servicio no cuenta con insumos vinculados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                supplies.forEach { supplyWithDetails ->
                    SupplyItemCard(supplyWithDetails = supplyWithDetails)
                }
            }
        }
    }
}

@Composable
private fun SupplyItemCard(
    supplyWithDetails: ServiceSupplyWithDetailsModel,
    modifier: Modifier = Modifier
) {
    val product = supplyWithDetails.product.product
    val unitTypeName = supplyWithDetails.product.unitType.name.ifBlank { "Sin unidad" }
    val brandName = product.brand.ifBlank { "Sin marca" }
    val quantity = supplyWithDetails.supply.quantityRequired

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.commercialName.ifBlank { "Insumo sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$unitTypeName • $brandName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = quantity.formatQuantity(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "cant.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// 4. Diálogo Informativo para Servicio Eliminado
// ==========================================
@Composable
fun DialogInformativeServiceEdition(
    modifier: Modifier = Modifier,
    name: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "El servicio $name se encuentra eliminado",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Este servicio se encuentra eliminado y su información no se puede modificar. Restablécelo para poder editarlo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = onDismiss
                ) {
                    Text("Entiendo")
                }
            }
        }
    }
}

// ==========================================
// Funciones auxiliares locales
// ==========================================
private fun Double.formatQuantity(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", this)
    }
}

private fun Double.formatPrice(): String {
    return String.format(Locale.US, "%.2f", this)
}

@PreviewLightDark
@Composable
private fun BodyDetailServicePreview() {
    AttiTheme {
        BodyDetailService(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            state = DetailServiceState(
                serviceWithDetails = ServiceWithDetailsModel(
                    service = ServiceModel(
                        id = "1",
                        name = "Vacunación Anual Canina",
                        salePrice = 50.00,
                        estimatedCost = 15.00,
                        status = 1
                    ),
                    category = AppCatalogModel(name = "Medicina Preventiva"),
                    supplies = listOf(
                        ServiceSupplyWithDetailsModel(
                            supply = ServiceSupplyModel(quantityRequired = 11.0),
                            product = ProductWithDetailsModel(
                                product = ProductModel(commercialName = "Jeringa 5ml", brand = "Desechable estéril"),
                                unitType = AppCatalogModel(name = "Unidad")
                            )
                        ),
                        ServiceSupplyWithDetailsModel(
                            supply = ServiceSupplyModel(quantityRequired = 1.0),
                            product = ProductWithDetailsModel(
                                product = ProductModel(commercialName = "Vacuna Antirrábica", brand = "Nobivac"),
                                unitType = AppCatalogModel(name = "Dosis 1.0 ml")
                            )
                        ),
                        ServiceSupplyWithDetailsModel(
                            supply = ServiceSupplyModel(quantityRequired = 2.0),
                            product = ProductWithDetailsModel(
                                product = ProductModel(commercialName = "Algodón", brand = "Torunda esterilizada"),
                                unitType = AppCatalogModel(name = "Unidad")
                            )
                        )
                    )
                )
            )
        )
    }
}