package com.anankastudio.videocollector.utilities

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.util.Locale

class Utils {

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getDeviceResolution(context: Context): Pair<Int, Int> {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    fun formatFileSize(size: Long): String {
        val kiloByte = 1024.0
        val megaByte = kiloByte * 1024
        val gigaByte = megaByte * 1024

        return when {
            size >= gigaByte -> String.format(Locale.US, "%.2f GB", size / gigaByte)
            size >= megaByte -> String.format(Locale.US, "%.2f MB", size / megaByte)
            size >= kiloByte -> String.format(Locale.US, "%.2f KB", size / kiloByte)
            else -> "$size B"
        }
    }

    fun formatFPS(value: Double, decimalPlaces: Int): String {
        require(decimalPlaces >= 0) { "Decimal cannot be negative number" }
        val roundedValue = String.format(Locale.US, "%.${decimalPlaces}f", value)
        return "$roundedValue FPS"
    }

    fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return if (minutes > 0) {
            String.format(Locale.US, "%d.%02d Minutes", minutes, remainingSeconds)
        } else {
            "$seconds Seconds"
        }
    }
}