package yosel.dev.atti.core.room.config

import androidx.room.Database
import androidx.room.RoomDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.client.ClientEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepDao
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepEntity
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service.ServiceEntity
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyEntity
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.room.tables.supplier.SupplierEntity

@Database(
    entities = [
        ClientEntity::class,
        PatientEntity::class,
        AppCatalogEntity::class,
        SupplierEntity::class,
        ProductEntity::class,
        ServiceEntity::class,
        ServiceSupplyEntity::class,
        ConsultationEntity::class,
        ConsultationTypeStepEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun patientDao(): PatientDao
    abstract fun appCatalogDao(): AppCatalogDao
    abstract fun supplierDao(): SupplierDao
    abstract fun productDao(): ProductDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceSupplyDao(): ServiceSupplyDao
    abstract fun consultationDao(): ConsultationDao
    abstract fun consultationTypeStepDao(): ConsultationTypeStepDao
}