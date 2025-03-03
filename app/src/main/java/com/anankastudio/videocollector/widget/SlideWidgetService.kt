package com.anankastudio.videocollector.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import com.anankastudio.videocollector.database.AppDatabase
import com.anankastudio.videocollector.models.room.WidgetVideo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SlideWidgetService {

    private val slideIndexes = mutableMapOf<Int, Int>()
    private var cacheSlides: List<WidgetVideo> = emptyList()

    fun loadSlides(context: Context, onLoaded: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            cacheSlides = db.widgetVideoDao().getAllWidgetVideo()
            withContext(Dispatchers.Main) {
                onLoaded?.invoke()
            }
        }
    }

    fun updateWidget(context: Context, appWidgetId: Int) {
        loadSlides(context) {
            if (cacheSlides.isNotEmpty()) {
                val currentIndex = (slideIndexes[appWidgetId] ?: 0) % cacheSlides.size
                slideIndexes[appWidgetId] = (currentIndex + 1) % cacheSlides.size
                val currentSlide = cacheSlides[currentIndex]
                val appWidgetManager = AppWidgetManager.getInstance(context)

                SlideWidgetHelper.updateWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    currentSlide
                )
            }
        }
    }

    fun showNextIndex(context: Context, appWidgetId: Int) {
        if (cacheSlides.isEmpty()) {
            loadSlides(context) {
                showNextIndexInternal(context, appWidgetId)
            }
        } else {
            showNextIndexInternal(context, appWidgetId)
        }
    }

    private fun showNextIndexInternal(context: Context, appWidgetId: Int) {
        if (cacheSlides.isNotEmpty()) {
            val nextIndex = ((slideIndexes[appWidgetId] ?: 0) + 1) % cacheSlides.size
            slideIndexes[appWidgetId] = nextIndex

            val nextSlide = cacheSlides[nextIndex]
            val appWidgetManager = AppWidgetManager.getInstance(context)

            SlideWidgetHelper.updateWidget(
                context,
                appWidgetManager,
                appWidgetId,
                nextSlide
            )
        }
    }
}