package com.anankastudio.videocollector.utilities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.anankastudio.videocollector.R
import javax.inject.Inject

class NotificationManager @Inject constructor(
    private val context: Context,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    var onPermissionGranted: (() -> Unit)? = null
    var onPermissionDenied: (() -> Unit)? = null
    var onShowPermissionRationale: (() -> Unit)? = null
    var onShowNotificationSetting: (() -> Unit)? = null

    fun showDownloadNotification(
        context: Context,
        title: String,
        message: String,
        videoUri: Uri
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "Download Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for download notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(videoUri, "video/mp4")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(R.drawable.ic_check)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun openAppSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        context.startActivity(intent)
    }

    fun checkNotificationPermission(
        requestPermissionLauncher: ActivityResultLauncher<String>,
        activity: Activity
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onPermissionGranted?.invoke()
            return
        }

        when {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted?.invoke()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                onShowPermissionRationale?.invoke()
            }

            else -> {
                val isFirstLaunch = sharedPreferencesManager.getBoolean(SharedPreferencesManager.IS_FIRST_LAUNCH, true)
                if (isFirstLaunch) {
                    requestPermission(requestPermissionLauncher)
                    sharedPreferencesManager.setBoolean(SharedPreferencesManager.IS_FIRST_LAUNCH, false)
                } else {
                    onShowNotificationSetting?.invoke()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermission(requestPermissionLauncher: ActivityResultLauncher<String>) {
        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }
}