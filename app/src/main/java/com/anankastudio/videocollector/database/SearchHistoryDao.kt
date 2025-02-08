package com.anankastudio.videocollector.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anankastudio.videocollector.models.room.SearchHistory

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    suspend fun getAllSearchHistory(): List<SearchHistory>

    @Query("SELECT COUNT(*) FROM search_history WHERE keyword = :keyword")
    suspend fun getKeywordCount(keyword: String): Int

    @Query("SELECT COUNT(*) FROM search_history")
    suspend fun getTotalSearchHistory(): Int

    @Query("DELETE FROM search_history WHERE id = (SELECT id FROM search_history ORDER BY timestamp ASC LIMIT 1)")
    suspend fun deleteOldestSearchHistory()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    suspend fun insertSearchHistoryWithCheck(searchHistory: SearchHistory) {
        val keywordCount = getKeywordCount(searchHistory.keyword ?: "")
        if (keywordCount == 0) {
            if (getTotalSearchHistory() == 10) {
                deleteOldestSearchHistory()
            }
            insertSearchHistory(searchHistory)
        }
    }
}