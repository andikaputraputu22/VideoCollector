package com.anankastudio.videocollector.injection

import android.content.Context
import com.anankastudio.videocollector.database.AppDatabase
import com.anankastudio.videocollector.database.DetailVideoDao
import com.anankastudio.videocollector.database.FavoriteVideoDao
import com.anankastudio.videocollector.database.SearchHistoryDao
import com.anankastudio.videocollector.database.WidgetVideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideDetailVideoDao(database: AppDatabase): DetailVideoDao {
        return database.detailVideoDao()
    }

    @Provides
    fun provideFavoriteVideoDao(database: AppDatabase): FavoriteVideoDao {
        return database.favoriteVideoDao()
    }

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    fun provideWidgetVideoDao(database: AppDatabase): WidgetVideoDao {
        return database.widgetVideoDao()
    }
}