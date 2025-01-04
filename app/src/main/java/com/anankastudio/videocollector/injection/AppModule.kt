package com.anankastudio.videocollector.injection

import android.content.Context
import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import com.anankastudio.videocollector.utilities.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }
}