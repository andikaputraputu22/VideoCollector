package com.anankastudio.videocollector.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    var page = 1
    var pageTotal = 1
    val loading by lazy { MutableLiveData<Boolean>() }
    val loadingMore by lazy { MutableLiveData<Boolean>() }

    private val _listVideo = MutableLiveData<List<Video>?>()
    val listVideo: LiveData<List<Video>?> = _listVideo

    fun getPopularVideo() {
        if (page > pageTotal) return
        if (page > 1) loadingMore.value = true else loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchPopularVideo(page)) {
                    is Result.Success -> {
                        val data = result.data
                        val perPage = data.perPage ?: 15
                        val totalPage = data.totalResults ?: 0
                        val totalResults: Double = totalPage/perPage.toDouble()
                        pageTotal = ceil(totalResults).toInt()
                        val listVideo = result.data.videos
                        _listVideo.postValue(listVideo)
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
}