package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.ClientDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ClientsDataSource @Inject constructor(
    private val postgrest: Postgrest
){
    suspend fun getAllClients(): List<ClientDto> {
        return postgrest.from(Constants.CLIENTS_SUPABASE)
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ClientDto>()
    }

    suspend fun insertAndGetClient(client: ClientDto): ClientDto {
        return postgrest.from(Constants.CLIENTS_SUPABASE)
            .insert(client) {
                select()
            }
            .decodeSingle<ClientDto>()
    }

    suspend fun updateClient(client: ClientDto) {
        postgrest.from(Constants.CLIENTS_SUPABASE)
            .update(client) {
                filter {
                    eq("id", client.id ?: "")
                }
            }
    }
}