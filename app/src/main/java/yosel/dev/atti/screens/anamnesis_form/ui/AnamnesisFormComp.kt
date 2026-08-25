package yosel.dev.atti.screens.anamnesis_form.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BodyAnamnesisForm(
    modifier: Modifier = Modifier,
    state: AnamnesisFormState,
    onAction: (AnamnesisFormAction) -> Unit
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {

        }
    }
}