package com.anankastudio.videocollector.utilities

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anankastudio.videocollector.R
import java.util.Locale

class Utils {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
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

    fun checkStoragePermission(activity: Activity, onGranted: () -> Unit) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                        Constants.STORAGE_PERMISSION_CODE
                    )
                } else {
                    onGranted()
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                onGranted()
            }
            else -> {
                val permissionsNeeded = mutableListOf<String>()
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (permissionsNeeded.isNotEmpty()) {
                    ActivityCompat.requestPermissions(
                        activity,
                        permissionsNeeded.toTypedArray(),
                        Constants.STORAGE_PERMISSION_CODE
                    )
                } else {
                    onGranted()
                }
            }
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        onGranted: () -> Unit,
        context: Context
    ) {
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                onGranted()
            } else {
                Toast.makeText(context, context.getString(R.string.alert_need_storage_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun copyUrlToClipboard(url: String, context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("url", url)
        clipboard.setPrimaryClip(clip)
    }

    fun shareContent(content: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(intent, "Share Link"))
    }

    fun shareFileVideo(videoUri: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(videoUri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Video"))
    }

    fun openExternalBrowser(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    }
}