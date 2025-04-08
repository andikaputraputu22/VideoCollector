package com.anankastudio.videocollector.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.interfaces.ExplorePage
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.models.item.ContentVideo
import com.anankastudio.videocollector.models.room.SearchHistory
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Constants
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val utils: Utils,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    var page = 1
    var query = ""
    var typeContent = Constants.TYPE_CONTENT_VIDEO
    var pageTotal = 1
    val loading by lazy { MutableLiveData<Boolean>() }
    val loadingMore by lazy { MutableLiveData<Boolean>() }
    val isDataAvailable by lazy { MutableLiveData<Boolean>() }

    private val _listVideo = MutableLiveData<List<ExplorePage>?>()
    val listVideo: LiveData<List<ExplorePage>?> = _listVideo

    private val _listSearchHistory = MutableLiveData<List<SearchHistory>?>()
    val listSearchHistory: LiveData<List<SearchHistory>?> = _listSearchHistory

    private val _isSearchActive = MutableLiveData<Boolean>()
    val isSearchActive: LiveData<Boolean> = _isSearchActive

    fun getCollectionVideo() {
        if (page > pageTotal) return
        if (page > 1) loadingMore.value = true else loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchInitialCollectionItems(page)) {
                    is Result.Success -> {
                        val data = result.data
                        val perPage = data.perPage ?: 15
                        val totalPage = data.totalResults ?: 0
                        val totalResults: Double = totalPage/perPage.toDouble()
                        pageTotal = ceil(totalResults).toInt()
                        isDataAvailable.value = data.items?.isNotEmpty()
                        _listVideo.postValue(data.items)
                        page++
                    }
                    is Result.Error -> {
                        pageTotal = -1
                        isDataAvailable.value = false
                    }
                    is Result.Loading -> {}
                }
            } finally {
                loading.value = false
                loadingMore.value = false
                _isSearchActive.postValue(false)
                getAllSearchHistory()
            }
        }
    }

    fun getSearchVideo() {
        if (page > pageTotal) return
        if (page > 1) loadingMore.value = true else loading.value = true
        viewModelScope.launch {
            try {
                val orientation = sharedPreferencesManager.getString(SharedPreferencesManager.FILTER_ORIENTATION)
                val size = sharedPreferencesManager.getString(SharedPreferencesManager.FILTER_SIZE)
                when(val result = videoRepository.fetchSearchVideo(page, query, orientation, size)) {
                    is Result.Success -> {
                        val data = result.data
                        val perPage = data.perPage ?: 15
                        val totalPage = data.totalResults ?: 0
                        val totalResults: Double = totalPage/perPage.toDouble()
                        pageTotal = ceil(totalResults).toInt()
                        val listVideo = data.videos
                        isDataAvailable.value = listVideo?.isNotEmpty()
                        _listVideo.postValue(processContentVideo(listVideo))
                        page++
                    }
                    is Result.Error -> {
                        pageTotal = -1
                        isDataAvailable.value = false
                    }
                    is Result.Loading -> {}
                }
            } finally {
                loading.value = false
                loadingMore.value = false
                _isSearchActive.postValue(true)
            }
        }
    }

    private fun getAllSearchHistory() {
        viewModelScope.launch {
            try {
                val data = videoRepository.fetchAllSearchHistory()
                _listSearchHistory.postValue(data)
            } catch (_: Exception) {}
        }
    }

    private fun processContentVideo(videos: List<Video>?): List<ExplorePage> {
        val content: MutableList<ExplorePage> = mutableListOf()
        videos?.forEach {
            val contentVideo = ContentVideo()
            contentVideo.item = it
            content.add(contentVideo)
        }

        return content
    }

    fun saveSearchKeyword(keyword: String) {
        viewModelScope.launch {
            try {
                val searchHistory = SearchHistory(
                    id = 0,
                    keyword = keyword,
                    timestamp = System.currentTimeMillis()
                )
                videoRepository.insertSearchHistory(searchHistory)
            } catch (_: Exception) {}
        }
    }

    fun onScrolledToEnd() {
        if (loading.value != true && loadingMore.value != true && page <= pageTotal) {
            loadingMore.value = true
            if (typeContent == Constants.TYPE_CONTENT_VIDEO) {
                getSearchVideo()
            } else {
                getCollectionVideo()
            }
        }
    }

    fun hideKeyboard(view: View) {
        view.clearFocus()
        utils.hideKeyboard(view)
    }

    fun isFilterExist(): Boolean {
        val orientation = sharedPreferencesManager.getString(SharedPreferencesManager.FILTER_ORIENTATION)
        val size = sharedPreferencesManager.getString(SharedPreferencesManager.FILTER_SIZE)
        return orientation != "" || size != ""
    }
}