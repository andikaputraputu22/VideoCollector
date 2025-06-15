package com.anankastudio.videocollector.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import com.anankastudio.videocollector.repository.AdRepository
import com.anankastudio.videocollector.utilities.PetalAdsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    private val adRepository: AdRepository,
    private val petalAdsHelper: PetalAdsHelper
) : ViewModel() {

    private lateinit var interstitialHelper: PetalAdsHelper.InterstitialHelper
    private var pendingAction: (() -> Unit)? = null
    private var setupActivityHashCode: Int? = null

    fun setupInterstitialAdIfNeeded(
        context: Context,
        activity: Activity,
        lifecycle: Lifecycle,
        adId: String
    ) {
        if (setupActivityHashCode == activity.hashCode()) {
            return
        }

        setupInterstitialAd(context, lifecycle, adId)
        setupActivityHashCode = activity.hashCode()
    }

    private fun setupInterstitialAd(
        context: Context,
        lifecycle: Lifecycle,
        adId: String
    ) {
        interstitialHelper = petalAdsHelper.createInterstitialHelper(context)
            .setAdConfiguration(adId = adId)
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