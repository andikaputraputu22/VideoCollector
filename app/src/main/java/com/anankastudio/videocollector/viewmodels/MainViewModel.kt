package com.anankastudio.videocollector.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.anankastudio.videocollector.utilities.SharedPreferencesManager
import com.anankastudio.videocollector.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val utils: Utils,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    var isHardNotificationRequest = false

    fun showExternalBrowser(url: String, context: Context) {
        utils.openExternalBrowser(url, context)
    }

    fun isAlreadyReadPrivacyPolicy() = sharedPreferencesManager.getBoolean(
        SharedPreferencesManager.IS_ALREADY_READ_PRIVACY_POLICY,
        false
    )

    fun setReadPrivacyPolicy() {
        sharedPreferencesManager.setBoolean(
            SharedPreferencesManager.IS_ALREADY_READ_PRIVACY_POLICY,
            true
        )
    }
}