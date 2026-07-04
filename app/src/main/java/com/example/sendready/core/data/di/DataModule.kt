package com.example.sendready.core.data.di

import com.example.sendready.core.data.repository.SettingsRepositoryImpl
import com.example.sendready.core.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
}

