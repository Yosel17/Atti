package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.ClientDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ClientsDataSource @Inject constructor(
    private val postgrest: Postgrest
){
    suspend fun getAllClients(): List<ClientDto> {
        return postgrest.from(Constants.CLIENTS_SUPABASE)
            .select()
            .decodeList<ClientDto>()
    }

    suspend fun insertAndGetClient(client: ClientDto): ClientDto {
        return postgrest.from(Constants.CLIENTS_SUPABASE)
            .insert(client) {
                select()
            }
            .decodeSingle<ClientDto>()
    }
}