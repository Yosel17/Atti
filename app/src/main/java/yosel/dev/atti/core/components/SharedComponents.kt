package yosel.dev.atti.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SnackBarError(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(12.dp),
        action = {
            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Cerrar notificación",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}