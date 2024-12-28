package com.anankastudio.videocollector.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.interfaces.ExplorePage
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.models.item.ContentVideo
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Constants
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val utils: Utils
) : ViewModel() {

    var page = 1
    var query = ""
    var typeContent = Constants.TYPE_CONTENT_VIDEO
    var pageTotal = 1
    val loading by lazy { MutableLiveData<Boolean>() }
    val loadingMore by lazy { MutableLiveData<Boolean>() }

    private val _listVideo = MutableLiveData<List<ExplorePage>?>()
    val listVideo: LiveData<List<ExplorePage>?> = _listVideo

    fun getCollectionVideo() {
        loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchAllCollectionItems(page)) {
                    is Result.Success -> {
                        val data = result.data
                        val perPage = data.perPage ?: 15
                        val totalPage = data.totalResults ?: 0
                        val totalResults: Double = totalPage/perPage.toDouble()
                        pageTotal = ceil(totalResults).toInt()
                        _listVideo.postValue(data.items)
                        page++
                    }
                    is Result.Error -> {
                        pageTotal = -1
                    }
                }
            } finally {
                loading.value = false
                loadingMore.value = false
            }
        }
    }

    fun getSearchVideo() {
        if (page > pageTotal) return
        if (page > 1) loadingMore.value = true else loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchSearchVideo(page, query)) {
                    is Result.Success -> {
                        val data = result.data
                        val perPage = data.perPage ?: 15
                        val totalPage = data.totalResults ?: 0
                        val totalResults: Double = totalPage/perPage.toDouble()
                        pageTotal = ceil(totalResults).toInt()
                        val listVideo = data.videos
                        _listVideo.postValue(processContentVideo(listVideo))
                        page++
                    }
                    is Result.Error -> {
                        pageTotal = -1
                    }
                }
            } finally {
                loading.value = false
                loadingMore.value = false
            }
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

    fun onScrolledToEnd() {
        if (loading.value != true && loadingMore.value != true && page <= pageTotal) {
            loadingMore.value = true
            getSearchVideo()
        }
    }

    fun hideKeyboard(activity: Activity) {
        utils.hideKeyboard(activity)
    }
}