package com.anankastudio.videocollector.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anankastudio.videocollector.models.Video
import com.anankastudio.videocollector.repository.AdRepository
import com.anankastudio.videocollector.repository.VideoRepository
import com.anankastudio.videocollector.repository.WidgetRepository
import com.anankastudio.videocollector.utilities.PetalAdsHelper
import com.anankastudio.videocollector.utilities.Result
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val widgetRepository: WidgetRepository,
    private val adRepository: AdRepository,
    private val utils: Utils,
    private val petalAdsHelper: PetalAdsHelper
) : ViewModel() {

    var page = 1
    var pageTotal = 1
    val loading by lazy { MutableLiveData<Boolean>() }
    val loadingMore by lazy { MutableLiveData<Boolean>() }
    val loadingWidget by lazy { MutableLiveData<Boolean>() }
    val isDataAvailable by lazy { MutableLiveData<Boolean>() }
    val isWidgetSetupSuccess by lazy { MutableLiveData<Boolean>() }

    private lateinit var interstitialHelper: PetalAdsHelper.InterstitialHelper
    private var pendingAction: (() -> Unit)? = null

    private val _listVideo = MutableLiveData<List<Video>?>()
    val listVideo: LiveData<List<Video>?> = _listVideo

    fun getWidgetVideo() {
        loadingWidget.value = true
        viewModelScope.launch {
            try {
                when(widgetRepository.fetchWidgetVideo()) {
                    is Result.Success -> {
                        isWidgetSetupSuccess.value = true
                    }
                    is Result.Error -> {
                        isWidgetSetupSuccess.value = false
                    }
                }
            } finally {
                loadingWidget.value = false
            }
        }
    }

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
                        val listVideo = data.videos
                        isDataAvailable.value = listVideo?.isNotEmpty()
                        _listVideo.postValue(listVideo)
                    }
                    is Result.Error -> {
                        pageTotal = -1
                        isDataAvailable.value = false
                    }
                }
            } finally {
                loading.value = false
                loadingMore.value = false
            }
        }
    }

    fun onScrolledToEnd() {
        if (loading.value != true && loadingMore.value != true && page <= pageTotal) {
            loadingMore.value = true
            getPopularVideo()
        }
    }

    fun sendEmailFeedback(context: Context) {
        utils.sendFeedback(context)
    }

    fun showExternalBrowser(url: String, context: Context) {
        utils.openExternalBrowser(url, context)
    }

    fun setupInterstitialAd(
        context: Context,
        lifecycle: Lifecycle
    ) {
        interstitialHelper = petalAdsHelper.createInterstitialHelper(context)
            .setAdConfiguration(adId = "testb4znbuh3n2")
            .loadAd(
                onAdLoaded = {},
                onAdFailed = {},
                onAdClosed = {
                    adRepository.markFirstAdShown()
                    adRepository.resetAdTrigger()
                    pendingAction?.invoke()
                    pendingAction = null
                }
            )

        lifecycle.addObserver(interstitialHelper)
    }

    private fun showInterstitialAndContinue(
        activity: Activity,
        action: () -> Unit
    ) {
        if (::interstitialHelper.isInitialized && interstitialHelper.isAdLoaded()) {
            pendingAction = action
            interstitialHelper.showAd(activity)
        } else {
            action()
        }
    }

    fun handleButtonClick(
        activity: Activity,
        action: () -> Unit
    ) {
        val isFirstTime = !adRepository.isFirstTimeAdShown()
        if (isFirstTime) {
            if (::interstitialHelper.isInitialized && interstitialHelper.isAdLoaded()) {
                showInterstitialAndContinue(activity, action)
            } else {
                action()
            }
            return
        }

        val currentClickCount = adRepository.getClickCount() + 1
        adRepository.setClickCount(currentClickCount)

        if (adRepository.shouldShowInterstitial()
            && ::interstitialHelper.isInitialized
            && interstitialHelper.isAdLoaded()) {
            showInterstitialAndContinue(activity, action)
        } else {
            action()
        }
    }
}