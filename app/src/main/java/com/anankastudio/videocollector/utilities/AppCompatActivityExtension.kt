package com.anankastudio.videocollector.utilities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.anankastudio.videocollector.activities.CollectionActivity
import com.anankastudio.videocollector.activities.MainActivity
import com.anankastudio.videocollector.viewmodels.AdViewModel

fun AppCompatActivity.setupAds(): AdViewModel {
    val adViewModel = ViewModelProvider(this)[AdViewModel::class.java]
    val adId = this.getAdId()
    adViewModel.setupInterstitialAdIfNeeded(this, this, lifecycle, adId)
    return adViewModel
}

fun AppCompatActivity.getAdId(): String {
    return when (this) {
        is MainActivity -> Constants.MAIN_INTERSTITIAL
        is CollectionActivity -> Constants.COLLECTION_INTERSTITIAL
        else -> Constants.DEFAULT_INTERSTITIAL
    }
}