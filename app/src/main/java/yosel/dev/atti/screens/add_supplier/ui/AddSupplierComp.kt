package yosel.dev.atti.screens.add_supplier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.InputFieldWithTextGlobal
import yosel.dev.atti.core.utils.Constants

@Composable
fun BodyAddSupplier(
    modifier: Modifier = Modifier,
    state: AddSupplierState,
    onAction: (AddSupplierAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        SupplierForm(
            modifier = Modifier.weight(1f),
            formState = state.formState,
            onInputChanged = { value, field ->
                onAction(AddSupplierAction.OnChangeValueFormState(value, field))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(AddSupplierAction.AddSupplier)
            },
            enabled = state.formState.isValid
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = "guardar proveedor"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Guardar proveedor")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SupplierForm(
    modifier: Modifier = Modifier,
    formState: AddSupplierFormState,
    onInputChanged: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Complete los datos para dar de alta a un nuevo proveedor.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 3.1 Nombre del proveedor
        item {
            InputFieldWithTextGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Nombre del proveedor",
                placeHolder = "ej. Distribuidora San Carlos",
                value = formState.name,
                onValueChange = {
                    onInputChanged(it, Constants.SUPPLIER_NAME_FIELD)
                },
                leadingIcon = Icons.Outlined.Business,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.SUPPLIER_NAME_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        // 3.2 Nit del proveedor
        item {
            InputFieldWithTextGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "NIT del proveedor",
                placeHolder = "ej. 1234567-8",
                value = formState.taxId,
                onValueChange = {
                    onInputChanged(it, Constants.SUPPLIER_TAX_ID_FIELD)
                },
                leadingIcon = Icons.Outlined.Badge,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.SUPPLIER_TAX_ID_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        // 3.3 Teléfono de contacto (sólo dígitos)
        item {
            InputFieldWithTextGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Teléfono de contacto",
                placeHolder = "ej. 55554444",
                value = formState.phoneNumber,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^\d*$"""))) {
                        onInputChanged(newValue, Constants.SUPPLIER_PHONE_FIELD)
                    }
                },
                leadingIcon = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.SUPPLIER_PHONE_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        // 3.4 Dirección Completa
        item {
            InputFieldWithTextGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Dirección completa",
                placeHolder = "ej. Calle Principal Z.1, Palencia",
                value = formState.address,
                onValueChange = {
                    onInputChanged(it, Constants.SUPPLIER_ADDRESS_FIELD)
                },
                leadingIcon = Icons.Outlined.Place,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                isError = formState.isError(Constants.SUPPLIER_ADDRESS_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}