package com.anankastudio.videocollector.utilities

import android.content.Context
import com.huawei.hms.ads.BannerAdSize
import java.util.Locale

object BannerAdSizeHelper {

    fun getRecommendedBannerSize(context: Context): BannerAdSize {
        val countryCode = Locale.getDefault().country.uppercase(Locale.ROOT)
        return if (countryCode == "CN") {
            BannerAdSize.BANNER_SIZE_360_57
        } else {
            BannerAdSize.BANNER_SIZE_320_50
        }
    }
}