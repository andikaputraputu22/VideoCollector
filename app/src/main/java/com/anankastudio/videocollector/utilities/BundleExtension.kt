package com.anankastudio.videocollector.utilities

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}