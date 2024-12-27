package com.anankastudio.videocollector.injection

import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideVideoRepository(apiService: ApiService): VideoRepository {
        return VideoRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideUtils(): Utils {
        return Utils()
    }
}