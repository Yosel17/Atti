package yosel.dev.atti.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.screens.add_client.data.AddClientRepositoryImpl
import yosel.dev.atti.screens.add_client.domain.AddClientRepository
import yosel.dev.atti.screens.navigation_bar.directory.data.DirectoryRepositoryImpl
import yosel.dev.atti.screens.navigation_bar.directory.domain.DirectoryRepository
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
}