package com.crrl.beatplayer.alertdialog.utils

import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape

object Utils {
    /**
     * This method draws a round rect shape.
     * @param width: int
     * @param height: int
     * @param color: int
     * @return ShapeDrawable
     */
    fun drawRoundRectShape(width: Int, height: Int, color: Int): ShapeDrawable {
        val r = floatArrayOf(30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f)
        val oval = ShapeDrawable(RoundRectShape(r, RectF(), r))
        oval.intrinsicHeight = height
        oval.intrinsicWidth = width
        oval.paint.color = color
        return oval
    }
}