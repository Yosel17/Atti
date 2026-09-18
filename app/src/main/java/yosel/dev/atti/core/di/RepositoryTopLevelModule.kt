package yosel.dev.atti.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.screens.top_level.clients.data.ClientsRepositoryImpl
import yosel.dev.atti.screens.top_level.clients.domain.ClientsRepository
import yosel.dev.atti.screens.top_level.consultation.data.ConsultationRepositoryImpl
import yosel.dev.atti.screens.top_level.consultation.domain.ConsultationRepository
import yosel.dev.atti.screens.top_level.home.data.HomeRepositoryImpl
import yosel.dev.atti.screens.top_level.home.domain.HomeRepository
import yosel.dev.atti.screens.top_level.inventory.data.InventoryRepositoryImpl
import yosel.dev.atti.screens.top_level.inventory.domain.InventoryRepository
import yosel.dev.atti.screens.top_level.patients.data.PatientsRepositoryImpl
import yosel.dev.atti.screens.top_level.patients.domain.PatientsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryTopLevelModule {

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        impl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindConsultationRepository(
        impl: ConsultationRepositoryImpl
    ): ConsultationRepository

    @Binds
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    abstract fun bindClientsRepository(
        impl: ClientsRepositoryImpl
    ): ClientsRepository

    @Binds
    abstract fun bindPatientsRepository(
        impl: PatientsRepositoryImpl
    ): PatientsRepository
}