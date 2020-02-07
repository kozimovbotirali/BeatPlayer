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

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.crrl.beatplayer.R
import com.crrl.beatplayer.utils.PlayerConstants

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
            .asBitmap()
            .load(uri)
            .error(R.drawable.ic_empty_cover)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) = Unit
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    view.setImageBitmap(resource)
                }
            })
    } else {
        Glide.with(view)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.ic_empty_cover)
            .error(R.drawable.ic_empty_cover)
            .into(view)
    }
}

@BindingAdapter("app:width", "app:height", requireAll = true)
fun setImageSize(view: ImageView, width: Int, height: Int) {
    view.layoutParams.apply {
        this.width = width
        this.height = height
    }
    view.scaleType = ImageView.ScaleType.CENTER_CROP
}
