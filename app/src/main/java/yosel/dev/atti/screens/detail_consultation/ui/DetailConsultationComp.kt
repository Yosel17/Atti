package yosel.dev.atti.screens.detail_consultation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import yosel.dev.atti.core.components.PatientConsultationHeaderCard

@Composable
fun BodyDetailConsultation(
    modifier: Modifier = Modifier,
    state: DetailConsultationState
) {
    Column(
        modifier = modifier
    ) {
        PatientConsultationHeaderCard(
            consultation = state.consultationWithDetails
        )
    }
}