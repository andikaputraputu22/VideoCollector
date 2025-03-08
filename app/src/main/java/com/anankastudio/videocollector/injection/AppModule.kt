package com.anankastudio.videocollector.injection

import android.content.Context
import com.anankastudio.videocollector.api.ApiService
import com.anankastudio.videocollector.database.DetailVideoDao
import com.anankastudio.videocollector.database.FavoriteVideoDao
import com.anankastudio.videocollector.database.SearchHistoryDao
import com.anankastudio.videocollector.database.WidgetVideoDao
import com.anankastudio.videocollector.repository.MediaRepository
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.repository.WidgetRepository
import com.anankastudio.videocollector.utilities.NotificationManager
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
    fun provideVideoRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
        detailVideoDao: DetailVideoDao,
        favoriteVideoDao: FavoriteVideoDao,
        searchHistoryDao: SearchHistoryDao,
        utils: Utils
    ): VideoRepository {
        return VideoRepository(context, apiService, detailVideoDao, favoriteVideoDao, searchHistoryDao, utils)
    }

    @Singleton
    @Provides
    fun provideWidgetRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
        widgetVideoDao: WidgetVideoDao
    ): WidgetRepository {
        return WidgetRepository(context, apiService, widgetVideoDao)
    }

    @Singleton
    @Provides
    fun provideMediaRepository(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager
    ): MediaRepository {
        return MediaRepository(context, notificationManager)
    }

    @Singleton
    @Provides
    fun provideUtils(): Utils {
        return Utils()
    }

    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        sharedPreferencesManager: SharedPreferencesManager
    ): NotificationManager {
        return NotificationManager(context, sharedPreferencesManager)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }
}