package yosel.dev.atti.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
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
}