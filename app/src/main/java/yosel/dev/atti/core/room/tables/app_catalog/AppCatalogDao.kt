package yosel.dev.atti.core.room.tables.app_catalog

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface AppCatalogDao {

    // --- LECTURAS
    @Query("SELECT * FROM app_catalogs ORDER BY name ASC")
    suspend fun getAllCatalogs(): List<AppCatalogEntity>

    @Query("SELECT * FROM app_catalogs WHERE id = :id LIMIT 1")
    suspend fun getCatalogById(id: Int): AppCatalogEntity?

    @Query("SELECT * FROM app_catalogs WHERE catalog_type_id = :catalogTypeId")
    suspend fun getCatalogsByTypeId(catalogTypeId: Int): List<AppCatalogEntity>

    @Query("SELECT * FROM app_catalogs WHERE catalog_type_id IN (:catalogTypeIds)")
    suspend fun getCatalogsByTypeIdList(catalogTypeIds: List<Int>): List<AppCatalogEntity>

    // --- ESCRITURAS ---
    @Upsert
    suspend fun insertCatalog(catalog: AppCatalogEntity)

    @Upsert
    suspend fun insertAllCatalogs(catalogs: List<AppCatalogEntity>)

    @Update
    suspend fun updateCatalog(catalog: AppCatalogEntity)

    // --- ELIMINACIONES ---
    @Query("DELETE FROM app_catalogs WHERE id = :id")
    suspend fun deleteCatalogById(id: Int)

    @Query("DELETE FROM app_catalogs")
    suspend fun deleteAllCatalogs()

    @Transaction
    suspend fun clearAndInsertCatalogs(catalogs: List<AppCatalogEntity>) {
        deleteAllCatalogs()
        insertAllCatalogs(catalogs)
    }
}