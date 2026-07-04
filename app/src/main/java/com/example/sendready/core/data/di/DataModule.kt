package com.example.sendready.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.sendready.core.data.local.room.AppDatabase
import com.example.sendready.core.data.local.room.HistoryDao
import com.example.sendready.core.data.repository.SettingsRepositoryImpl
import com.example.sendready.core.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: com.example.sendready.core.data.repository.MediaRepositoryImpl
    ): com.example.sendready.core.domain.repository.MediaRepository

    @Binds
    abstract fun bindVideoQueueRepository(
        videoQueueRepositoryImpl: com.example.sendready.core.data.repository.VideoQueueRepositoryImpl
    ): com.example.sendready.core.domain.repository.VideoQueueRepository

    @Binds
    abstract fun bindCompressionHistoryRepository(
        compressionHistoryRepositoryImpl: com.example.sendready.core.data.repository.CompressionHistoryRepositoryImpl
    ): com.example.sendready.core.domain.repository.CompressionHistoryRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "sendready_db"
            ).fallbackToDestructiveMigration().build()
        }

        @Provides
        fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
            return appDatabase.historyDao()
        }
    }
}

