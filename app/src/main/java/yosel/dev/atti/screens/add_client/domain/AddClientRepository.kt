package yosel.dev.atti.screens.add_client.domain

import yosel.dev.atti.core.models.model.ClientModel

interface AddClientRepository {

    suspend fun insertClient(client: ClientModel): Result<Unit>
}