package com.anankastudio.videocollector.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WidgetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetId = intent.getIntExtra("appWidgetId", AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            when(intent.action) {
                "ACTION_NEXT_SLIDE" -> {
                    SlideWidgetService.showNextIndex(context, appWidgetId)
                }
                else -> {
                    SlideWidgetService.updateWidget(context, appWidgetId)
                }
            }
        }
    }
}