package yosel.dev.atti.core.models.filter

import androidx.compose.runtime.Immutable
import yosel.dev.atti.core.utils.Constants

enum class DateSortOrder(val label: String) {
    NEWEST("Más reciente"),
    OLDEST("Más antiguo")
}

enum class StatusFilter(val label: String, val statusCode: Int?) {
    ALL("Todos", null),
    ACTIVE("Activo", Constants.ACTIVE_STATUS),
    DELETED("Eliminado", Constants.DELETED_STATUS)
}

enum class NeuteredFilter(val label: String, val value: Boolean?) {
    ALL("Todos", null),
    YES("Sí", true),
    NO("No", false)
}

@Immutable
data class ClientFilter(
    val status: StatusFilter = StatusFilter.ALL,
    val dateSort: DateSortOrder = DateSortOrder.NEWEST
) {
    val isActive: Boolean get() = status != StatusFilter.ALL || dateSort != DateSortOrder.NEWEST
}

@Immutable
data class PatientFilter(
    val speciesId: Int? = null,
    val genderId: Int? = null,
    val neutered: NeuteredFilter = NeuteredFilter.ALL,
    val status: StatusFilter = StatusFilter.ALL,
    val dateSort: DateSortOrder = DateSortOrder.NEWEST
) {
    val isActive: Boolean
        get() = speciesId != null || genderId != null || neutered != NeuteredFilter.ALL ||
                status != StatusFilter.ALL || dateSort != DateSortOrder.NEWEST
}

@Immutable
data class ProductFilter(
    val categoryId: Int? = null,
    val unitTypeId: Int? = null,
    val supplierId: String? = null,
    val status: StatusFilter = StatusFilter.ALL,
    val dateSort: DateSortOrder = DateSortOrder.NEWEST
) {
    val isActive: Boolean
        get() = categoryId != null || unitTypeId != null || supplierId != null ||
                status != StatusFilter.ALL || dateSort != DateSortOrder.NEWEST
}

@Immutable
data class ServiceFilter(
    val categoryId: Int? = null,
    val status: StatusFilter = StatusFilter.ALL,
    val dateSort: DateSortOrder = DateSortOrder.NEWEST
) {
    val isActive: Boolean
        get() = categoryId != null || status != StatusFilter.ALL || dateSort != DateSortOrder.NEWEST
}

@Immutable
data class SupplierFilter(
    val status: StatusFilter = StatusFilter.ALL,
    val dateSort: DateSortOrder = DateSortOrder.NEWEST
) {
    val isActive: Boolean get() = status != StatusFilter.ALL || dateSort != DateSortOrder.NEWEST
}