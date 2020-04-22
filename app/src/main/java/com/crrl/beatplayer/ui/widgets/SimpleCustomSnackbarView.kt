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
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ViewSnackbarSimpleBinding
import com.crrl.beatplayer.extensions.animateScale
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.google.android.material.snackbar.ContentViewCallback


class SimpleCustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private var binding: ViewSnackbarSimpleBinding =
        inflateWithBinding(R.layout.view_snackbar_simple, true)

    init {
        clipToPadding = false
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        binding.icon.animateScale(0f, 1f, 500)
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }

    fun bind(
        message: String,
        listener: OnClickListener?,
        icon: Int,
        action: String?,
        backgroundId: Int
    ) {
        binding.apply {
            this.message.text = message
            action?.let {
                if (!it.isNullOrEmpty()) {
                    this.action.visibility = View.VISIBLE
                    this.action.text = it
                    this.action.setOnClickListener {
                        listener?.onClick(this.action)
                    }
                }
            }
            this.icon.setImageResource(icon)
            this.snackParent.setBackgroundResource(backgroundId)
        }
    }
}