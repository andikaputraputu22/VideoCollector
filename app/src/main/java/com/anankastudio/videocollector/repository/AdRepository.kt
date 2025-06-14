package com.anankastudio.videocollector.repository

import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import javax.inject.Inject

class AdRepository @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    private val interstitialMinTimeBeforeAd = 1 * 60 * 1000L
    private val interstitialClicksBeforeAd = 3

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
        sharedPreferencesManager.setInt(
            SharedPreferencesManager.INTERSTITIAL_CLICK_COUNT,
            count
        )
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
        val timeConditionMet = timeElapsed >= interstitialMinTimeBeforeAd
        val clickConditionMet = clickCount >= interstitialClicksBeforeAd

        return timeConditionMet && clickConditionMet
    }
}