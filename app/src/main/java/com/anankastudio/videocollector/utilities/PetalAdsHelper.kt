package com.anankastudio.videocollector.utilities

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.banner.BannerView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetalAdsHelper @Inject constructor() {

    companion object {
        private const val TAG = "PetalAdsHelper"
    }

    fun createBannerHelper(bannerView: BannerView): BannerHelper {
        return BannerHelper(bannerView)
    }

    inner class BannerHelper(private val bannerView: BannerView): DefaultLifecycleObserver {
        private var adLoadedCallback: (() -> Unit)? = null
        private var adFailedCallback: ((Int) -> Unit)? = null
        private var adClickedCallback: (() -> Unit)? = null

        fun setAdConfiguration(
            adId: String,
            bannerAdSize: BannerAdSize = BannerAdSize.BANNER_SIZE_360_57,
            refreshIntervalSeconds: Long = 60
        ): BannerHelper {
            bannerView.adId = adId
            bannerView.bannerAdSize = bannerAdSize
            bannerView.setBannerRefresh(refreshIntervalSeconds)

            return this
        }

        fun loadAd(
            onAdLoaded: (() -> Unit)? = null,
            onAdFailed: ((errorCode: Int) -> Unit)? = null,
            onAdClicked: (() -> Unit)? = null
        ): BannerHelper {
            this.adLoadedCallback = onAdLoaded
            this.adFailedCallback = onAdFailed
            this.adClickedCallback = onAdClicked

            setupAdListener()
            loadBannerAd()

            return this
        }

        private fun setupAdListener() {
            bannerView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    adLoadedCallback?.invoke()
                }

                override fun onAdFailed(errorCode: Int) {
                    adFailedCallback?.invoke(errorCode)
                }

                override fun onAdClicked() {
                    adClickedCallback?.invoke()
                }
            }
        }

        private fun loadBannerAd() {
            val adParam = AdParam.Builder().build()
            bannerView.loadAd(adParam)
        }

        fun refreshAd() {
            loadBannerAd()
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            bannerView.resume()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            bannerView.pause()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            bannerView.destroy()
        }
    }
}