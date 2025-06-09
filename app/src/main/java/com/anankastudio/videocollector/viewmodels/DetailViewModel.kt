package com.anankastudio.videocollector.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.interfaces.DetailPage
import com.anankastudio.videocollector.models.item.ContentDetailInfo
import com.anankastudio.videocollector.models.item.ContentDetailProfile
import com.anankastudio.videocollector.models.item.ContentDetailVideo
import com.anankastudio.videocollector.models.room.DetailVideo
import com.anankastudio.videocollector.repository.MediaRepository
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val mediaRepository: MediaRepository,
    private val utils: Utils,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val contents: MutableList<DetailPage> = mutableListOf()
    val loading by lazy { MutableLiveData<Boolean>() }

    private val _listContent = MutableLiveData<MutableList<DetailPage>>()
    val listContent: LiveData<MutableList<DetailPage>> = _listContent

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _isVideoOnFavorite = MutableLiveData<Boolean>()
    val isVideoOnFavorite: LiveData<Boolean> = _isVideoOnFavorite

    private val _downloadStatus = MutableLiveData<ResultStatus>()
    val downloadStatus: LiveData<ResultStatus> = _downloadStatus

    var dataDetailVideo: DetailVideo? = null
    var onFavorite: Boolean = false
    var onShareFileVideo: Boolean = false

    private val minTimeBeforeAd = 1 * 60 * 1000L
    private val clicksBeforeAd = 3

    fun getDetailVideo(id: Long) {
        loading.value = true
        viewModelScope.launch {
            try {
                when(val result = videoRepository.fetchDetailVideo(id)) {
                    is Result.Success -> {
                        val data = result.data
                        dataDetailVideo = data
                        _title.postValue(data?.userName ?: "")
                        data?.let { checkVideoOnFavorite(it.id) }
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

    private suspend fun checkVideoOnFavorite(id: Long) {
        if (videoRepository.isVideoExists(id)) {
            _isVideoOnFavorite.postValue(true)
            onFavorite = true
        } else {
            _isVideoOnFavorite.postValue(false)
            onFavorite = false
        }
    }

    fun saveVideoToFavorite(data: DetailVideo) {
        viewModelScope.launch {
            try {
                videoRepository.saveVideoToFavorite(data)
            } catch (_: Exception) {}
            checkVideoOnFavorite(data.id)
        }
    }

    fun deleteVideoFromFavorite(id: Long) {
        viewModelScope.launch {
            try {
                videoRepository.deleteVideoFromFavorite(id)
            } catch (_: Exception) {}
            checkVideoOnFavorite(id)
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

    fun downloadVideo(context: Context, url: String, isForShare: Boolean = false) {
        _downloadStatus.postValue(ResultStatus.Prepare(isForShare))
        mediaRepository.downloadVideo(context, url, isForShare, onSuccess = {
            _downloadStatus.postValue(ResultStatus.Success)
            if (isForShare) {
                onShareFileVideo = false
                shareFileVideo(it, context)
            }
        }, onError = { e ->
            _downloadStatus.postValue(ResultStatus.Error(e))
        })
    }

    fun copyUrlVideo(url: String, context: Context) {
        utils.copyUrlToClipboard(url, context)
    }

    fun shareContent(url: String, context: Context) {
        utils.shareContent(url, context)
    }

    private fun shareFileVideo(videoUri: String, context: Context) {
        utils.shareFileVideo(videoUri, context)
    }

    fun showCreatorProfile(url: String, context: Context) {
        utils.openExternalBrowser(url, context)
    }

    // Interstitial Ads
    fun markFirstAdShown() {
        val currentTime = System.currentTimeMillis()
        sharedPreferencesManager.apply {
            setBoolean(SharedPreferencesManager.INTERSTITIAL_FIRST_AD_SHOWN, true)
            setLong(SharedPreferencesManager.INTERSTITIAL_LAST_AD_TIME, currentTime)
            setInt(SharedPreferencesManager.INTERSTITIAL_CLICK_COUNT, 0)
        }
    }

    fun resetAdTrigger() {
        val currentTime = System.currentTimeMillis()
        sharedPreferencesManager.apply {
            setLong(SharedPreferencesManager.INTERSTITIAL_LAST_AD_TIME, currentTime)
            setInt(SharedPreferencesManager.INTERSTITIAL_CLICK_COUNT, 0)
        }
    }

    fun isFirstTimeAdShown() = sharedPreferencesManager.getBoolean(
        SharedPreferencesManager.INTERSTITIAL_FIRST_AD_SHOWN,
        false
    )

    fun getClickCount() = sharedPreferencesManager.getInt(
        SharedPreferencesManager.INTERSTITIAL_CLICK_COUNT,
        0
    )

    private fun getLastAdTime() = sharedPreferencesManager.getLong(
        SharedPreferencesManager.INTERSTITIAL_LAST_AD_TIME,
        0
    )

    fun setClickCount(count: Int) {
        sharedPreferencesManager.setInt(SharedPreferencesManager.INTERSTITIAL_CLICK_COUNT, count)
    }

    fun shouldShowInterstitial(): Boolean {
        val isFirstTime = !isFirstTimeAdShown()
        if (isFirstTime) {
            return false
        }

        val currentTime = System.currentTimeMillis()
        val lastAdTime = getLastAdTime()
        val clickCount = getClickCount()

        val timeElapsed = currentTime - lastAdTime
        val timeConditionMet = timeElapsed >= minTimeBeforeAd
        val clickConditionMet = clickCount >= clicksBeforeAd

        return timeConditionMet && clickConditionMet
    }
}

sealed class ResultStatus {
    data class Prepare(val isForShare: Boolean): ResultStatus()
    data object Success: ResultStatus()
    data class Error(val exception: Exception): ResultStatus()
}