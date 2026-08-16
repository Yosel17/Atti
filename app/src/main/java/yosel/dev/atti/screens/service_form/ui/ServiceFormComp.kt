package yosel.dev.atti.screens.service_form.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AppCatalogSelector
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.utils.Constants

@Composable
fun BodyServiceForm(
    modifier: Modifier = Modifier,
    state: ServiceFormState,
    onAction: (ServiceFormAction) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        GeneralInformationSection(
            formInputState = state.formInputState,
            onAction = onAction
        )
        Spacer(modifier = Modifier.height(28.dp))
        PricesAndCostsSection(
            formInputState = state.formInputState,
            onAction = onAction
        )
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun GeneralInformationSection(
    modifier: Modifier = Modifier,
    formInputState: ServiceFormInputsState,
    onAction: (ServiceFormAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Información General",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información General",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InputFieldGlobal(
                label = "Nombre del Servicio",
                placeholder = "Ej. Consulta General Canina",
                value = formInputState.name,
                onValueChange = {
                    onAction(
                        ServiceFormAction.OnChangeValueFormInputState(
                            value = it,
                            field = Constants.SERVICE_NAME_FIELD
                        )
                    )
                },
                leadingIcon = Icons.Filled.MedicalServices,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = formInputState.isError(Constants.SERVICE_NAME_FIELD),
                errorMessage = "El nombre del servicio es obligatorio"
            )
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle(
                title = "Categoría",
                icon = Icons.Filled.Category,
                showIcon = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppCatalogSelector(
                selectedCatalog = formInputState.selectedCategory,
                onOpenSheet = {
                    onAction(ServiceFormAction.OnOpenCategorySheet)
                },
                icon = Icons.Filled.Category,
                emptyText = "Selecciona una categoría"
            )
        }
    }
}

@Composable
private fun PricesAndCostsSection(
    modifier: Modifier = Modifier,
    formInputState: ServiceFormInputsState,
    onAction: (ServiceFormAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Payments,
                    contentDescription = "Precios y Costos",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Precios y Costos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            InputFieldGlobal(
                label = "Precio de venta",
                placeholder = "0.00",
                value = formInputState.salePrice,
                onValueChange = { input ->
                    val sanitizedInput = input.replace(',', '.')
                    if (sanitizedInput.matches(Regex("^(\\d*(\\.\\d{0,2})?)?$"))) {
                        onAction(
                            ServiceFormAction.OnChangeValueFormInputState(
                                value = sanitizedInput,
                                field = Constants.SERVICE_SALE_PRICE_FIELD
                            )
                        )
                    }
                },
                leadingIcon = Icons.Filled.PointOfSale,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                isError = formInputState.isError(Constants.SERVICE_SALE_PRICE_FIELD),
                errorMessage = "El precio de venta es obligatorio"
            )
        }
    }
}