package com.anankastudio.videocollector.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.item.ContentDetailInfo
import com.anankastudio.videocollector.models.item.ContentDetailProfile
import com.anankastudio.videocollector.models.item.ContentDetailVideo
import com.anankastudio.videocollector.models.room.DetailVideo
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val utils: Utils
) : ViewModel() {

    private val contents: MutableList<DetailPage> = mutableListOf()
    val loading by lazy { MutableLiveData<Boolean>() }

    private val _listContent = MutableLiveData<MutableList<DetailPage>>()
    val listContent: LiveData<MutableList<DetailPage>> = _listContent

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    fun getDetailVideo(id: Long) {
        loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchDetailVideo(id)) {
                    is Result.Success -> {
                        val data = result.data
                        _title.postValue(data?.userName ?: "")
                        setupContentDetail(data)
                    }
                    is Result.Error -> {

                    }
                }
            } finally {
                loading.value = false
            }
        }
    }

    private fun setupContentDetail(data: DetailVideo?) {
        contents.clear()
        data?.let {
            setVideo(contents, it)
            setCreator(contents, it)
            setInfo(contents, it)
        }
        _listContent.postValue(contents)
    }

    private fun setVideo(
        contents: MutableList<DetailPage>,
        data: DetailVideo
    ) {
        val contentDetailVideo = ContentDetailVideo(
            item = videoRepository.getBestVideoForDevice(data.videoFiles)
        )
        contents.add(contentDetailVideo)
    }

    private fun setCreator(
        contents: MutableList<DetailPage>,
        data: DetailVideo
    ) {
        val contentDetailProfile = ContentDetailProfile(
            userName = data.userName,
            userUrl = data.userUrl
        )
        contents.add(contentDetailProfile)
    }

    private fun setInfo(
        contents: MutableList<DetailPage>,
        data: DetailVideo
    ) {
        val highestVideoFile = videoRepository.getHighestVideo(data.videoFiles)
        val contentDetailInfo = ContentDetailInfo(
            fileSize = highestVideoFile?.size?.let { utils.formatFileSize(it) },
            fps = highestVideoFile?.fps?.let { utils.formatFPS(it, 0) },
            duration = data.duration?.let { utils.formatDuration(it) },
            videoFile = highestVideoFile
        )
        contents.add(contentDetailInfo)
    }
}