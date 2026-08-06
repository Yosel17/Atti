package yosel.dev.atti.screens.add_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.InputFieldGlobal
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.ui.theme.AttiTheme

@Composable
fun BodyAddClient(
    modifier: Modifier = Modifier,
    state: AddClientState,
    onAction: (AddClientAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
    ) {
        ClientForm(
            modifier = Modifier.weight(1f),
            formState = state.formState,
            onInputChanged = { value, field ->
                onAction(AddClientAction.OnChangeValueFormState(value, field))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                onAction(AddClientAction.AddClient)
            },
            enabled = state.formState.isValid
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = "registrar cliente"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Registrar cliente")
            }
        }
    }
}

@Composable
private fun ClientForm(
    modifier: Modifier,
    formState: AddClientFormState,
    onInputChanged: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            Text(
                text = "Complete los datos para dar de alta a un nuevo cliente.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            InputFieldGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Nombres",
                value = formState.firstName,
                onValueChange = {
                    onInputChanged(it, Constants.FIRST_NAME_FIELD)
                },
                leadingIcon = Icons.Outlined.Person,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.FIRST_NAME_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        item {
            InputFieldGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Apellidos",
                value = formState.lastName,
                onValueChange = {
                    onInputChanged(it, Constants.LAST_NAME_FIELD)
                },
                leadingIcon = Icons.Outlined.Person,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.LAST_NAME_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        item {
            InputFieldGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Nit",
                value = formState.documentId,
                onValueChange = {
                    onInputChanged(it, Constants.DOCUMENT_ID_FIELD)
                },
                leadingIcon = Icons.Outlined.Badge,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.DOCUMENT_ID_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        item {
            InputFieldGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Teléfono",
                value = formState.phoneNumber,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("""^\d*$"""))) {
                        onInputChanged(it, Constants.PHONE_NUMBER_FIELD)
                    }
                },
                leadingIcon = Icons.Outlined.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.PHONE_NUMBER_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }

        item {
            InputFieldGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Email (Opcional)",
                value = formState.email,
                onValueChange = {
                    onInputChanged(it, Constants.EMAIL_FIELD)
                },
                leadingIcon = Icons.Outlined.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = formState.isError(Constants.EMAIL_FIELD),
                errorMessage = null
            )
        }

        item {
            InputFieldGlobal(
                modifier = Modifier.fillMaxWidth(),
                label = "Dirección",
                value = formState.address,
                onValueChange = {
                    onInputChanged(it, Constants.ADDRESS_FIELD)
                },
                leadingIcon = Icons.Outlined.Place,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                isError = formState.isError(Constants.ADDRESS_FIELD),
                errorMessage = "Este campo no puede estar vacío"
            )
        }
    }
}

@PreviewLightDark
@Composable
fun BodyPreview() {
    AttiTheme {
        BodyAddClient(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            state = AddClientState(),
            onAction = {}
        )
    }
}