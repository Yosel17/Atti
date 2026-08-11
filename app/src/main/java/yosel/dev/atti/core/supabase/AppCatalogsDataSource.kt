package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.AppCatalogDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class AppCatalogsDataSource @Inject constructor(
    private val postGres: Postgrest
){
    suspend fun getCatalogsByTypes(types: List<Int>): List<AppCatalogDto> {
        if (types.isEmpty()) return emptyList()

        return postGres.from(Constants.APP_CATALOGS_SUPABASE)
            .select {
                filter {
                    isIn("catalog_type_id", types)
                    eq("is_active", true)
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