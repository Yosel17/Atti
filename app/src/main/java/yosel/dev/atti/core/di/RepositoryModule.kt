package yosel.dev.atti.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.screens.clients.data.ClientsRepositoryImpl
import yosel.dev.atti.screens.clients.domain.ClientsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindClientsRepository(
        impl: ClientsRepositoryImpl
    ): ClientsRepository
}