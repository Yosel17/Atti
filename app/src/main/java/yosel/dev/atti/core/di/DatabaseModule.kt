package yosel.dev.atti.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.TABLE_NAME
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Singleton
    @Provides
    fun provideClientDao(appDatabase: AppDatabase): ClientDao = appDatabase.clientDao()

    @Singleton
    @Provides
    fun providePatientDao(appDatabase: AppDatabase): PatientDao = appDatabase.patientDao()

    @Singleton
    @Provides
    fun provideAppCatalogDao(appDatabase: AppDatabase): AppCatalogDao = appDatabase.appCatalogDao()

    @Singleton
    @Provides
    fun provideSupplierDao(appDatabase: AppDatabase): SupplierDao = appDatabase.supplierDao()

    @Singleton
    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao = appDatabase.productDao()

    @Singleton
    @Provides
    fun provideServiceDao(appDatabase: AppDatabase): ServiceDao = appDatabase.serviceDao()

    @Singleton
    @Provides
    fun provideServiceSupplyDao(appDatabase: AppDatabase): ServiceSupplyDao = appDatabase.serviceSupplyDao()

    @Singleton
    @Provides
    fun provideConsultationDao(appDatabase: AppDatabase): ConsultationDao = appDatabase.consultationDao()

    @Singleton
    @Provides
    fun provideConsultationTypeStepDao(appDatabase: AppDatabase): ConsultationTypeStepDao = appDatabase.consultationTypeStepDao()
}