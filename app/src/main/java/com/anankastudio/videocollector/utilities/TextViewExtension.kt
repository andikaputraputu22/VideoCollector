package com.anankastudio.videocollector.utilities

import android.graphics.Paint
import android.widget.TextView

fun TextView.addUnderline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}