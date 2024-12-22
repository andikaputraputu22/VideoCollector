package com.anankastudio.videocollector

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class VideoCollector : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}