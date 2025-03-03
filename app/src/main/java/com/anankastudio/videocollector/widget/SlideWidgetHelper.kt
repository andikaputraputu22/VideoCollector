package com.anankastudio.videocollector.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.activities.DetailVideoActivity
import com.anankastudio.videocollector.models.room.WidgetVideo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget

object SlideWidgetHelper {

    fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        data: WidgetVideo
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_slideshow)
        views.setTextViewText(R.id.widgetTitle, data.title)

        val appWidgetTarget = AppWidgetTarget(context.applicationContext, R.id.widgetImage, views, appWidgetId)
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(data.image)
            .into(appWidgetTarget)

        val intent = Intent(context, WidgetAlarmReceiver::class.java).apply {
            action = "ACTION_NEXT_SLIDE"
            putExtra("appWidgetId", appWidgetId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pendingIntentDetailVideo = getDetailVideo(context, data.id)
        views.setOnClickPendingIntent(R.id.widgetNext, pendingIntent)
        views.setOnClickPendingIntent(R.id.rootView, pendingIntentDetailVideo)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getDetailVideo(
        context: Context,
        videoId: Long
    ): PendingIntent {
        val intent = Intent(context, DetailVideoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(DetailVideoActivity.EXTRA_ID_VIDEO, videoId)
        }

        return PendingIntent.getActivity(
            context,
            videoId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun sendWidgetUpdateBroadcast(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, SlideWidgetProvider::class.java)
        )

        appWidgetIds.forEach { appWidgetId ->
            val intent = Intent(context, WidgetAlarmReceiver::class.java).apply {
                action = "ACTION_UPDATE_WIDGET"
                putExtra("appWidgetId", appWidgetId)
            }
            context.sendBroadcast(intent)
        }
    }
}