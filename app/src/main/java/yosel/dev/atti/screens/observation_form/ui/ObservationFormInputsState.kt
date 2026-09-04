package yosel.dev.atti.screens.observation_form.ui

data class ObservationFormInputsState(
    val observation: String = ""
) {
    val isValid: Boolean
        get() = observation.isNotBlank()

    fun hasChangesFrom(initial: ObservationFormInputsState): Boolean {
        return observation.trim() != initial.observation.trim()
    }
}
