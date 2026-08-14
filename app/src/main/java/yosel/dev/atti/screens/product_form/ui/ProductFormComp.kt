package yosel.dev.atti.screens.product_form.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Straighten
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
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.AppCatalogSelector
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.components.SectionTitle
import yosel.dev.atti.core.utils.Constants

@Composable
fun BodyProductForm(
    modifier: Modifier = Modifier,
    state: ProductFormState,
    onAction: (ProductFormAction) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            BasicInformationSection(
                formInputState = state.formInputState,
                onAction = onAction
            )
        }

    }
}

@Composable
private fun BasicInformationSection(
    modifier: Modifier = Modifier,
    formInputState: ProductFormInputsState,
    onAction: (ProductFormAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Informacion",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información Básica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputFieldGlobal(
                label = "Nombre comercial",
                placeholder = "Ej: Antibiótico Amoxipet",
                value = formInputState.commercialName,
                onValueChange = {
                    onAction(ProductFormAction.OnChangeValueFormInputState(
                        value = it,
                        field = Constants.PRODUCT_COMMERCIAL_NAME_FIELD)
                    )
                },
                leadingIcon = Icons.Filled.Badge,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = formInputState.isError(Constants.PRODUCT_COMMERCIAL_NAME_FIELD),
                errorMessage = "El nombre comercial es obligatorio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFieldGlobal(
                label = "Marca",
                placeholder = "Ej: BioVet Labs",
                value = formInputState.brand,
                onValueChange = {
                    onAction(ProductFormAction.OnChangeValueFormInputState(
                        value = it,
                        field = Constants.PRODUCT_BRAND_FIELD)
                    )
                },
                leadingIcon = Icons.Filled.Storefront,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = formInputState.isError(Constants.PRODUCT_BRAND_FIELD),
                errorMessage = "El nombre de la marca es obligatorio"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(
                title = "Categoria",
                icon = Icons.Filled.Category,
                showIcon = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppCatalogSelector(
                selectedCatalog = formInputState.selectedCategory,
                onOpenSheet = {
                    onAction(ProductFormAction.OnOpenCategorySheet)
                },
                icon = Icons.Filled.Category,
                emptyText = "Selecciona una categoria"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(
                title = "Unidad de medida",
                icon = Icons.Filled.Straighten,
                showIcon = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppCatalogSelector(
                selectedCatalog = formInputState.selectedUnitType,
                onOpenSheet = {
                    onAction(ProductFormAction.OnOpenUnitsMeasurementSheet)
                },
                icon = Icons.Filled.Straighten,
                emptyText = "Selecciona una unidad de medida"
            )
        }
    }
}