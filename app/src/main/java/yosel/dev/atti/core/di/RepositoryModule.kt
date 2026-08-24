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
import yosel.dev.atti.screens.anamnesis_form.data.AnamnesisFormRepositoryImpl
import yosel.dev.atti.screens.anamnesis_form.domain.AnamnesisFormRepository
import yosel.dev.atti.screens.detail_client.data.DetailClientRepositoryImpl
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import yosel.dev.atti.screens.detail_consultation.data.DetailConsultationRepositoryImpl
import yosel.dev.atti.screens.detail_consultation.domain.DetailConsultationRepository
import yosel.dev.atti.screens.detail_patient.data.DetailPatientRepositoryImpl
import yosel.dev.atti.screens.detail_patient.domain.DetailPatientRepository
import yosel.dev.atti.screens.detail_product.data.DetailProductRepositoryImpl
import yosel.dev.atti.screens.detail_product.domain.DetailProductRepository
import yosel.dev.atti.screens.detail_service.data.DetailServiceRepositoryImpl
import yosel.dev.atti.screens.detail_service.domain.DetailServiceRepository
import yosel.dev.atti.screens.detail_supplier.data.DetailSupplierRepositoryImpl
import yosel.dev.atti.screens.detail_supplier.domain.DetailSupplierRepository
import yosel.dev.atti.screens.navigation_bar.consultation.data.ConsultationRepositoryImpl
import yosel.dev.atti.screens.navigation_bar.consultation.domain.ConsultationRepository
import yosel.dev.atti.screens.navigation_bar.directory.data.DirectoryRepositoryImpl
import yosel.dev.atti.screens.navigation_bar.directory.domain.DirectoryRepository
import yosel.dev.atti.screens.navigation_bar.inventory.data.InventoryRepositoryImpl
import yosel.dev.atti.screens.navigation_bar.inventory.domain.InventoryRepository
import yosel.dev.atti.screens.product_form.data.ProductFormRepositoryImpl
import yosel.dev.atti.screens.product_form.domain.ProductFormRepository
import yosel.dev.atti.screens.service_form.data.ServiceFormRepositoryImpl
import yosel.dev.atti.screens.service_form.domain.ServiceFormRepository
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

    @Binds
    @Singleton
    abstract fun bindDetailSupplierRepository(
        impl: DetailSupplierRepositoryImpl
    ): DetailSupplierRepository

    @Binds
    @Singleton
    abstract fun bindProductFormRepository(
        impl: ProductFormRepositoryImpl
    ): ProductFormRepository

    @Binds
    @Singleton
    abstract fun bindDetailProductRepository(
        impl: DetailProductRepositoryImpl
    ): DetailProductRepository

    @Binds
    @Singleton
    abstract fun bindServiceFormRepository(
        impl: ServiceFormRepositoryImpl
    ): ServiceFormRepository

    @Binds
    @Singleton
    abstract fun bindDetailServiceRepository(
        impl: DetailServiceRepositoryImpl
    ): DetailServiceRepository

    @Binds
    @Singleton
    abstract fun bindConsultationRepository(
        impl: ConsultationRepositoryImpl
    ): ConsultationRepository

    @Binds
    @Singleton
    abstract fun bindDetailConsultationRepository(
        impl: DetailConsultationRepositoryImpl
    ): DetailConsultationRepository

    @Binds
    @Singleton
    abstract fun bindAnamnesisFormRepository(
        impl: AnamnesisFormRepositoryImpl
    ): AnamnesisFormRepository
}