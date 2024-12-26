package com.anankastudio.videocollector.utilities

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

class Utils {

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}