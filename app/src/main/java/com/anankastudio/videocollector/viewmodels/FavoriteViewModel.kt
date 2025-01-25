package com.anankastudio.videocollector.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.models.room.FavoriteVideo
import com.anankastudio.videocollector.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    val loading by lazy { MutableLiveData<Boolean>() }

    private val _listFavoriteVideo = MutableLiveData<List<FavoriteVideo>?>()
    val listFavoriteVideo: LiveData<List<FavoriteVideo>?> = _listFavoriteVideo

    fun getAllFavoriteVideo() {
        loading.value = true
        viewModelScope.launch {
            try {
                val data = videoRepository.fetchAllFavoriteVideo()
                _listFavoriteVideo.postValue(data)
            } finally {
                loading.value = false
            }
        }
    }

    fun deleteAllFavoriteVideo() {
        viewModelScope.launch {
            try {
                videoRepository.deleteAllFavoriteVideo()
            } catch (_: Exception) {}
            getAllFavoriteVideo()
        }
    }
}