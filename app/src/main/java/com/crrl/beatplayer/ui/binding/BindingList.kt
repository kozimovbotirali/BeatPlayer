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

package com.crrl.beatplayer.ui.binding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

@BindingAdapter(
    "app:overScroll",
    "app:overScrollOrientation",
    "app:staticOverScroll",
    requireAll = false
)
fun setOverScroll(
    view: View,
    overScroll: Boolean,
    overScrollOrientation: Int = 0,
    staticOverScroll: Boolean = false
) {
    if (!overScroll) return
    when (view) {
        is ViewPager -> OverScrollDecoratorHelper.setUpOverScroll(view)
        else -> {
            if (overScrollOrientation > 1 || overScrollOrientation < 0)
                throw IllegalStateException("The orientation can only be either 0 or 1.")
            if (staticOverScroll) {
                OverScrollDecoratorHelper.setUpStaticOverScroll(view, overScrollOrientation)
            } else {
                OverScrollDecoratorHelper.setUpOverScroll(
                    view as RecyclerView,
                    overScrollOrientation
                )
            }
        }
    }
}