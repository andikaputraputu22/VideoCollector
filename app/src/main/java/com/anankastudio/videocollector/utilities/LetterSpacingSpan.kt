package com.anankastudio.videocollector.utilities

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class LetterSpacingSpan(private val letterSpacing: Float): MetricAffectingSpan() {

    override fun updateDrawState(p0: TextPaint?) {
        p0?.letterSpacing = letterSpacing
    }

    override fun updateMeasureState(p0: TextPaint) {
        p0.letterSpacing = letterSpacing
    }
}