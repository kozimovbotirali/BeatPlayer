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

package com.crrl.beatplayer.alertdialog

import android.content.Context
import android.graphics.Canvas
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

        canvas.drawRoundRect(
            getDimensionInPixel(2).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(6).toFloat(),
            (height - 15).toFloat(),
            30f, 30f,
            paint
        )
        canvas.drawRoundRect(
            getDimensionInPixel(7).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(11).toFloat(),
            (height - 15).toFloat(),
            30f, 30f,
            paint
        )
        canvas.drawRoundRect(
            getDimensionInPixel(12).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(16).toFloat(),
            (height - 15).toFloat(),
            30f, 30f,
            paint
        )
        canvas.drawRoundRect(
            getDimensionInPixel(17).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(21).toFloat(),
            (height - 15).toFloat(),
            30f, 30f,
            paint
        )
        canvas.drawRoundRect(
            getDimensionInPixel(22).toFloat(),
            (height - (40 + random.nextInt((height / 1.5f).toInt() - 25))).toFloat(),
            getDimensionInPixel(26).toFloat(),
            (height - 15).toFloat(),
            30f, 30f,
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