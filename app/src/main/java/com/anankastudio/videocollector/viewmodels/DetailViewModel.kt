package com.anankastudio.videocollector.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.item.ContentDetailPreviewVideo
import com.anankastudio.videocollector.models.item.ContentDetailVideo
import com.anankastudio.videocollector.models.room.DetailVideo
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {

    private val contents: MutableList<DetailPage> = mutableListOf()
    val loading by lazy { MutableLiveData<Boolean>() }

    private val _listContent = MutableLiveData<MutableList<DetailPage>>()
    val listContent: LiveData<MutableList<DetailPage>> = _listContent

    fun getDetailVideo(id: Long) {
        loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchDetailVideo(id)) {
                    is Result.Success -> {
                        val data = result.data
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
            setPreviewVideo(contents, it)
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

    private fun setPreviewVideo(
        contents: MutableList<DetailPage>,
        data: DetailVideo
    ) {
        val contentDetailPreviewVideo = ContentDetailPreviewVideo(
            width = data.width,
            height = data.height,
            items = data.videoPictures
        )
        contents.add(contentDetailPreviewVideo)
    }
}