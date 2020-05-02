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

package com.crrl.beatplayer.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.crrl.beatplayer.R
import java.util.*

class MusicVisualizer : View {

    private var random = Random()

    private var paint = Paint()
    private val animateView = object : Runnable {
        override fun run() {
            postDelayed(this, 120)
            invalidate()
        }
    }

    constructor(context: Context) : super(context) {
        MusicVisualizer(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val att = context.obtainStyledAttributes(attrs, R.styleable.MusicVisualizer)
        paint.color = att.getColor(0, context.getColor(R.color.colorPrimary))
        removeCallbacks(animateView)
        post(animateView)
        att.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL

        canvas.drawRect(
            getDimensionInPixel(0).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(7).toFloat(),
            (height - 15).toFloat(),
            paint
        )
        canvas.drawRect(
            getDimensionInPixel(10).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(17).toFloat(),
            (height - 15).toFloat(),
            paint
        )
        canvas.drawRect(
            getDimensionInPixel(20).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(27).toFloat(),
            (height - 15).toFloat(),
            paint
        )
    }

    private fun getDimensionInPixel(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            removeCallbacks(animateView)
            post(animateView)
        } else if (visibility == GONE) {
            removeCallbacks(animateView)
        }
    }

    fun setTint(color: Int){
        paint.color = color
    }
}