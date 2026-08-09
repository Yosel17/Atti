package yosel.dev.atti.core.utils

import yosel.dev.atti.core.models.dto.AppCatalogDto
import yosel.dev.atti.core.models.dto.ClientDto
import yosel.dev.atti.core.models.dto.PatientDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.client.ClientEntity
import yosel.dev.atti.core.room.tables.client.ClientWithPatientsEntity
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.screens.add_client.ui.AddClientFormState
import yosel.dev.atti.screens.detail_client.ui.EditClientFormState
import java.text.Normalizer

fun ClientDto.toEntity() = ClientEntity(
    id = id.orEmpty(),
    firstName = firstName,
    lastName = lastName,
    documentId = documentId.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    email = email.orEmpty(),
    address = address.orEmpty(),
    createdAt = createdAt.orEmpty()
)


fun ClientEntity.toModel() = ClientModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt
)

fun ClientModel.toEntity() = ClientEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt
)

fun ClientModel.toDtoForInsert() = ClientDto(
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email.ifBlank { null },
    address = address,
)

fun ClientModel.toDtoForUpdate() = ClientDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email.ifBlank { null },
    address = address,
)

// DTO -> Entity
fun PatientDto.toEntity() = PatientEntity(
    id = id.orEmpty(),
    clientId = clientId,
    name = name,
    speciesId = speciesId ?: 0,
    genderId = genderId ?: 0,
    breed = breed.orEmpty(),
    ageYears = ageYears ?: 0,
    ageMonths = ageMonths ?: 0,
    color = color.orEmpty(),
    isNeutered = isNeutered ?: false,
    photoUrl = photoUrl.orEmpty(),
    createdAt = createdAt.orEmpty()
)

// Entity -> Model
fun PatientEntity.toModel() = PatientModel(
    id = id,
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
    photoUrl = photoUrl,
    createdAt = createdAt
)

fun PatientModel.toEntity() = PatientEntity(
    id = id,
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
    photoUrl = photoUrl,
    createdAt = createdAt
)

fun PatientModel.toDtoInsert() = PatientDto(
    clientId = clientId,
    name = name,
    speciesId = speciesId,
    genderId = genderId,
    breed = breed,
    ageYears = ageYears,
    ageMonths = ageMonths,
    color = color,
    isNeutered = isNeutered,
)

fun AddClientFormState.toModel() = ClientModel(
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address
)

fun ClientModel.toEditFormState() = EditClientFormState(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt
)

fun EditClientFormState.toModel() = ClientModel(
    id = id,
    firstName = firstName,
    lastName = lastName,
    documentId = documentId,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    createdAt = createdAt
)

fun ClientWithPatientsEntity.toModel() = ClientWithPatientsModel(
    client = client.toModel(),
    patients = patients.map { it.toModel() }
)

fun AppCatalogDto.toEntity() = AppCatalogEntity(
    id = id ?: 0,
    catalogTypeId = catalogTypeId,
    name = name,
    description = description.orEmpty(),
    isActive = isActive ?: true,
    createdAt = createdAt.orEmpty()
)

fun AppCatalogDto.toModel() = AppCatalogModel(
    id = id ?: 0,
    catalogTypeId = catalogTypeId,
    name = name,
    description = description.orEmpty(),
    isActive = isActive ?: true,
    createdAt = createdAt.orEmpty()
)

fun AppCatalogModel.toDtoForInsert() = AppCatalogDto(
    catalogTypeId = catalogTypeId,
    name = name,
    description = description.ifBlank { null },
    isActive = isActive
)

fun String.normalize(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase()
}