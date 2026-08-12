package yosel.dev.atti.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.screens.add_client.data.AddClientRepositoryImpl
import yosel.dev.atti.screens.add_client.domain.AddClientRepository
import yosel.dev.atti.screens.add_patient.data.AddPatientRepositoryImpl
import yosel.dev.atti.screens.add_patient.domain.AddPatientRepository
import yosel.dev.atti.screens.add_supplier.data.AddSupplierRepositoryImpl
import yosel.dev.atti.screens.add_supplier.domain.AddSupplierRepository
import yosel.dev.atti.screens.detail_client.data.DetailClientRepositoryImpl
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import yosel.dev.atti.screens.detail_patient.data.DetailPatientRepositoryImpl
import yosel.dev.atti.screens.detail_patient.domain.DetailPatientRepository
import yosel.dev.atti.screens.navigation_bar.directory.data.DirectoryRepositoryImpl
import yosel.dev.atti.screens.navigation_bar.directory.domain.DirectoryRepository
import yosel.dev.atti.screens.navigation_bar.inventory.data.InventoryRepositoryImpl
import yosel.dev.atti.screens.navigation_bar.inventory.domain.InventoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDirectoryRepository(
        impl: DirectoryRepositoryImpl
    ): DirectoryRepository

    @Binds
    @Singleton
    abstract fun bindAddClientRepository(
        impl: AddClientRepositoryImpl
    ): AddClientRepository

    @Binds
    @Singleton
    abstract fun bindDetailClientRepository(
        impl: DetailClientRepositoryImpl
    ): DetailClientRepository

    @Binds
    @Singleton
    abstract fun bindAddPatientRepository(
        impl: AddPatientRepositoryImpl
    ): AddPatientRepository

    @Binds
    @Singleton
    abstract fun bindDetailPatientRepository(
        impl: DetailPatientRepositoryImpl
    ): DetailPatientRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        impl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindAddSupplierRepository(
        impl: AddSupplierRepositoryImpl
    ): AddSupplierRepository
}