package yosel.dev.atti.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.screens.directory.data.DirectoryRepositoryImpl
import yosel.dev.atti.screens.directory.domain.DirectoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDirectoryRepository(
        impl: DirectoryRepositoryImpl
    ): DirectoryRepository
}