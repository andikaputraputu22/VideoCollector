package com.anankastudio.videocollector.utilities

import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.InterstitialAd
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

    fun createInterstitialHelper(context: Context): InterstitialHelper {
        return InterstitialHelper(context)
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

    inner class InterstitialHelper(private val context: Context): DefaultLifecycleObserver {
        private var interstitialAd: InterstitialAd? = null
        private var adId: String? = null

        private var adLoadedCallback: (() -> Unit)? = null
        private var adFailedCallback: ((Int) -> Unit)? = null
        private var adClosedCallback: (() -> Unit)? = null
        private var adClickedCallback: (() -> Unit)? = null
        private var adShownCallback: (() -> Unit)? = null


        fun setAdConfiguration(adId: String): InterstitialHelper {
            this.adId = adId
            return this
        }

        fun loadAd(
            onAdLoaded: (() -> Unit)? = null,
            onAdFailed: ((errorCode: Int) -> Unit)? = null,
            onAdClicked: (() -> Unit)? = null,
            onAdClosed: (() -> Unit)? = null,
            onAdShown: (() -> Unit)? = null
        ): InterstitialHelper {
            this.adLoadedCallback = onAdLoaded
            this.adFailedCallback = onAdFailed
            this.adClickedCallback = onAdClicked
            this.adClosedCallback = onAdClosed
            this.adShownCallback = onAdShown

            loadInterstitialAd()

            return this
        }

        private fun loadInterstitialAd() {
            val adId = this.adId ?: return
            interstitialAd = InterstitialAd(context).apply {
                this.adId = adId
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        adLoadedCallback?.invoke()
                    }

                    override fun onAdFailed(errorCode: Int) {
                        adFailedCallback?.invoke(errorCode)
                    }

                    override fun onAdClicked() {
                        adClickedCallback?.invoke()
                    }

                    override fun onAdClosed() {
                        adClosedCallback?.invoke()
                        loadInterstitialAd()
                    }

                    override fun onAdOpened() {
                        adShownCallback?.invoke()
                    }
                }

                val adParam = AdParam.Builder().build()
                loadAd(adParam)
            }
        }

        fun showAd(activity: Activity): Boolean {
            return if (isAdLoaded()) {
                interstitialAd?.show(activity)
                true
            } else {
                false
            }
        }

        fun isAdLoaded(): Boolean {
            return interstitialAd?.isLoaded == true
        }

        fun refreshAd() {
            loadInterstitialAd()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            interstitialAd = null
        }
    }
}