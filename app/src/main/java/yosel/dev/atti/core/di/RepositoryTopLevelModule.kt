package yosel.dev.atti.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.screens.top_level.clients.data.ClientsRepositoryImpl
import yosel.dev.atti.screens.top_level.clients.domain.ClientsRepository
import yosel.dev.atti.screens.top_level.consultation.data.ConsultationRepositoryImpl
import yosel.dev.atti.screens.top_level.consultation.domain.ConsultationRepository
import yosel.dev.atti.screens.top_level.consultations.data.ConsultationsRepositoryImpl
import yosel.dev.atti.screens.top_level.consultations.domain.ConsultationsRepository
import yosel.dev.atti.screens.top_level.home.data.HomeRepositoryImpl
import yosel.dev.atti.screens.top_level.home.domain.HomeRepository
import yosel.dev.atti.screens.top_level.patients.data.PatientsRepositoryImpl
import yosel.dev.atti.screens.top_level.patients.domain.PatientsRepository
import yosel.dev.atti.screens.top_level.products.data.ProductsRepositoryImpl
import yosel.dev.atti.screens.top_level.products.domain.ProductsRepository
import yosel.dev.atti.screens.top_level.receipts.data.ReceiptsRepositoryImpl
import yosel.dev.atti.screens.top_level.receipts.domain.ReceiptsRepository
import yosel.dev.atti.screens.top_level.services.data.ServicesRepositoryImpl
import yosel.dev.atti.screens.top_level.services.domain.ServicesRepository
import yosel.dev.atti.screens.top_level.suppliers.data.SuppliersRepositoryImpl
import yosel.dev.atti.screens.top_level.suppliers.domain.SuppliersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryTopLevelModule {

    @Binds
    @Singleton
    abstract fun bindConsultationRepository(
        impl: ConsultationRepositoryImpl
    ): ConsultationRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindClientsRepository(
        impl: ClientsRepositoryImpl
    ): ClientsRepository

    @Binds
    @Singleton
    abstract fun bindPatientsRepository(
        impl: PatientsRepositoryImpl
    ): PatientsRepository

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        impl: ProductsRepositoryImpl
    ): ProductsRepository

    @Binds
    @Singleton
    abstract fun bindServicesRepository(
        impl: ServicesRepositoryImpl
    ): ServicesRepository

    @Binds
    @Singleton
    abstract fun bindSuppliersRepository(
        impl: SuppliersRepositoryImpl
    ): SuppliersRepository

    @Binds
    @Singleton
    abstract fun bindReceiptsRepository(
        impl: ReceiptsRepositoryImpl
    ): ReceiptsRepository

    @Binds
    @Singleton
    abstract fun bindConsultationsRepository(
        impl: ConsultationsRepositoryImpl
    ): ConsultationsRepository
}