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

import android.app.Activity
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.Placeholder
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.toast
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import rm.com.audiowave.AudioWaveView
import rm.com.audiowave.OnSamplingListener

var placeholder: Drawable ? = null

@BindingAdapter("app:albumId", "app:recycled", requireAll = false)
fun setAlbumId(view: ImageView, albumId: Long, recyclerPlaceholder: Boolean = false){
    view.clipToOutline = true
    val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, albumId)
    if(recyclerPlaceholder){
        placeholder = view.drawable
        Glide.with(view)
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(placeholder)
            .error(R.drawable.ic_empty_cover)
            .into(view)
    }else{
        Glide.with(view)
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.ic_empty_cover)
            .error(R.drawable.ic_empty_cover)
            .into(view)
    }
}

@BindingAdapter("app:data")
fun setData(view: AudioWaveView, song: Song){
    Thread{
        (view.context as MainActivity).runOnUiThread{
            view.progress = 0F
            view.setRawData(ByteArray(Int.SIZE_BYTES))
        }
        val data = GeneralUtils.audio2Raw(song.path)
        try {
            (view.context as MainActivity).runOnUiThread {
                if (data == null) {
                    (view.context as Activity).toast("File Not Found", Toast.LENGTH_SHORT)
                    (view.context as MainActivity).viewModel.next(song)
                    return@runOnUiThread
                }else{
                    view.setRawData(data, object : OnSamplingListener {
                        override fun onComplete() = Unit
                    })
                }
            }
        } catch (e: IllegalStateException) {
            Log.println(Log.ERROR, "IllegalStateException", e.message!!)
        }
    }.start()
}