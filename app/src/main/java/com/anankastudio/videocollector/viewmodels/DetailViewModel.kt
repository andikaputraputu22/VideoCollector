package com.anankastudio.videocollector.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    val loading by lazy { MutableLiveData<Boolean>() }

    fun getDetailVideo(id: Long) {
        loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchDetailVideo(id)) {
                    is Result.Success -> {
                        val data = result.data

                    }
                    is Result.Error -> {

                    }
                }
            } finally {
                loading.value = false
            }
        }
    }
}