package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.ClientDto
import javax.inject.Inject

class ClientsDataSource @Inject constructor(
    private val postgrest: Postgrest
){
    suspend fun getAllClients(): List<ClientDto> {
        return postgrest.from("clients")
            .select()
            .decodeList<ClientDto>()
    }
}