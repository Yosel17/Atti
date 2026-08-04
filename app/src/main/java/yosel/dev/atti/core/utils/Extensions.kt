package yosel.dev.atti.core.utils

import yosel.dev.atti.core.models.dto.ClientDto
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.room.tables.client.ClientEntity

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