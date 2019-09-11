/*
 * Copyright 2019 Carlos René Ramos López. All rights reserved.
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

import android.content.ContentUris
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crrl.beatplayer.R
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.PlayerConstants
import kotlinx.android.synthetic.main.activity_settings.view.*
import rm.com.audiowave.AudioWaveView

var placeholder: Drawable? = GradientDrawable()

/**
 * @param view is the target view.
 * @param albumId is the id that will be used to get the image form the DB.
 * @param recyclerPlaceholder, if it is true the placeholder will be the last image setted.
 * */
@BindingAdapter("app:albumId", "app:recycled", requireAll = false)
fun setAlbumId(view: ImageView, albumId: Long, recyclerPlaceholder: Boolean = false) {
    view.clipToOutline = true
    val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, albumId)
    if (recyclerPlaceholder) {
        Glide.with(view)
            .load(uri)
            .placeholder(placeholder)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    placeholder = view.resources.getDrawable(R.drawable.ic_empty_cover, view.context.theme)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    placeholder = resource
                    return false
                }

            })
            .error(R.drawable.ic_empty_cover)
            .into(view)
    } else {
        Glide.with(view)
            .load(uri)
            .placeholder(R.drawable.ic_empty_cover)
            .error(R.drawable.ic_empty_cover)
            .into(view)
    }
}
