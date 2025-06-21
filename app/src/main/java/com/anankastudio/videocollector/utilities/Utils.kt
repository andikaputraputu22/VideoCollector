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
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anankastudio.videocollector.BuildConfig
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.interfaces.OnPermissionCallback
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

    fun checkStoragePermission(
        sharedPreferencesManager: SharedPreferencesManager,
        activity: Activity,
        onGranted: () -> Unit,
        onPermissionCallback: OnPermissionCallback
    ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_MEDIA_VIDEO)) {
                        onPermissionCallback.showPermissionExplanationDialog(onGranted)
                    } else {
                        if (isFirstTimeAskingPermission(sharedPreferencesManager, Manifest.permission.READ_MEDIA_VIDEO)) {
                            requestStoragePermissions(activity, onGranted)
                            setFirstTimeAskingPermission(
                                sharedPreferencesManager,
                                Manifest.permission.READ_MEDIA_VIDEO
                            )
                        } else {
                            onPermissionCallback.showPermissionDeniedDialog()
                        }
                    }
                } else {
                    onGranted()
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                onGranted()
            }
            else -> {
                val permissionsNeeded = mutableListOf<String>()
                val permissionsPermanentlyDenied = mutableListOf<String>()

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        && !isFirstTimeAskingPermission(sharedPreferencesManager, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        permissionsPermanentlyDenied.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && !isFirstTimeAskingPermission(sharedPreferencesManager, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permissionsPermanentlyDenied.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }

                when {
                    permissionsPermanentlyDenied.isNotEmpty() -> {
                        onPermissionCallback.showPermissionDeniedDialog()
                    }
                    permissionsNeeded.isNotEmpty() -> {
                        val shouldShowRationale = permissionsNeeded.any { permission ->
                            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                        }

                        if (shouldShowRationale) {
                            onPermissionCallback.showPermissionExplanationDialog(onGranted)
                        } else {
                            permissionsNeeded.forEach {
                                setFirstTimeAskingPermission(sharedPreferencesManager, it)
                            }
                            requestStoragePermissions(activity, onGranted)
                        }
                    }
                    else -> {
                        onGranted()
                    }
                }
            }
        }
    }

    fun requestStoragePermissions(
        activity: Activity,
        onGranted: () -> Unit
    ) {
        if (activity is OnPermissionCallback) {
            activity.storePendingCallback(onGranted)
        }

        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
            }
            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

        ActivityCompat.requestPermissions(
            activity,
            permissions,
            Constants.STORAGE_PERMISSION_CODE
        )
    }

    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        onGranted: () -> Unit,
        context: Context,
        onPermissionCallback: OnPermissionCallback
    ) {
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {
            when {
                grantResults.isEmpty() -> {
                    Toast.makeText(context, context.getString(R.string.alert_need_storage_permission), Toast.LENGTH_SHORT).show()
                }
                grantResults.all { it == PackageManager.PERMISSION_GRANTED } -> {
                    onGranted()
                }
                else -> {
                    val activity = context as Activity
                    val permissions = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                            arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
                        }
                        else -> {
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                    }

                    val canShowRationale = permissions.any { permission ->
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                    }

                    if (canShowRationale) {
                        onPermissionCallback.showPermissionExplanationDialog(onGranted)
                    } else {
                        val isActuallyDenied = permissions.any { permission ->
                            ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
                        }

                        if (isActuallyDenied) {
                            onPermissionCallback.showPermissionDeniedDialog()
                        } else {
                            onGranted()
                        }
                    }
                }
            }
        }
    }

    private fun isFirstTimeAskingPermission(
        sharedPreferencesManager: SharedPreferencesManager,
        permission: String
    ): Boolean {
        return sharedPreferencesManager.getBoolean(
            permission,
            true
        )
    }

    private fun setFirstTimeAskingPermission(
        sharedPreferencesManager: SharedPreferencesManager,
        permission: String
    ) {
        sharedPreferencesManager.setBoolean(permission, false)
    }

    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
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

    fun sendFeedback(context: Context) {
        val recipient = "ananka.studio@gmail.com"
        val subject = "Vliloom Feedback"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "No email application installed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getVersionName(context: Context): String {
        val isDebug = if (BuildConfig.DEBUG) " (debug)" else ""
        val versionName = "${context.getString(R.string.version)} ${BuildConfig.VERSION_NAME}$isDebug"

        return versionName
    }
}