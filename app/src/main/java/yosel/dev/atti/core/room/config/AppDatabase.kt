package yosel.dev.atti.core.room.config

import androidx.room.Database
import androidx.room.RoomDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.client.ClientEntity
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.room.tables.patient.PatientEntity

@Database(
    entities = [ClientEntity::class, PatientEntity::class, AppCatalogEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun clientDao(): ClientDao

    abstract fun patientDao(): PatientDao

    abstract fun appCatalogDao(): AppCatalogDao
}