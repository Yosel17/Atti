package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.AppCatalogDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class AppCatalogsDataSource @Inject constructor(
    private val postGres: Postgrest
){
    suspend fun getCatalogsByType(type: Int): List<AppCatalogDto>{
        return postGres.from(Constants.APP_CATALOGS_SUPABASE)
            .select {
                filter {
                    eq("catalog_type_id", type)
                }
            }
            .decodeList<AppCatalogDto>()
    }

    suspend fun insertAndGetCatalog(catalog: AppCatalogDto): AppCatalogDto {
        return postGres.from(Constants.APP_CATALOGS_SUPABASE)
            .insert(catalog) {
                select()
            }
            .decodeSingle<AppCatalogDto>()
    }
}