/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.alertdialog.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

object ViewUtils {
    /**
     * This method draws a round rect shape.
     * @param width: int
     * @param height: int
     * @param color: int
     * @return ShapeDrawable
     */
    fun drawRoundRectShape(
        width: Int,
        height: Int,
        color: Int,
        radius: Float = 30f
    ): ShapeDrawable {
        val r = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val oval = ShapeDrawable(RoundRectShape(r, RectF(), r))
        oval.intrinsicHeight = height
        oval.intrinsicWidth = width
        oval.paint.color = color
        return oval
    }
}

fun setLightStatusBar(activity: Activity?) {
    activity ?: return

    val flags = activity.window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    println("HOLA $flags")

    activity.window.decorView.systemUiVisibility = flags
}

fun clearLightStatusBar(activity: Activity?) {
    activity ?: return

    val flags = activity.window.decorView.systemUiVisibility xor SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    println("HOLA $flags")

    activity.window.decorView.systemUiVisibility = flags
}