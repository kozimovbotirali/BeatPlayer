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

import android.annotation.SuppressLint
import android.content.ContentUris
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.setMargins
import com.crrl.beatplayer.extensions.toggleShow
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import com.crrl.beatplayer.utils.PlayerConstants.ALBUM_TYPE
import com.crrl.beatplayer.utils.PlayerConstants.ARTIST_TYPE
import com.crrl.beatplayer.utils.PlayerConstants.FAVORITE_TYPE
import com.crrl.beatplayer.utils.PlayerConstants.FOLDER_TYPE
import com.github.florent37.kotlin.pleaseanimate.please


/**
 * @param view is the target view.
 * @param albumId is the id that will be used to get the image form the DB.
 * @param recyclerPlaceholder, if it is true the placeholder will be the last image setted.
 * */
@BindingAdapter("app:albumId", "app:recycled", requireAll = false)
fun setAlbumId(view: ImageView, albumId: Long, recyclerPlaceholder: Boolean = false) {
    view.clipToOutline = true
    val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, albumId)
    Glide.with(view)
        .asBitmap()
        .load(uri)
        .transition(withCrossFade())
        .placeholder(R.drawable.album_cover_frame)
        .error(R.drawable.ic_empty_cover)
        .into(view)
}

@BindingAdapter("app:width", "app:height", requireAll = true)
fun setImageSize(view: ImageView, width: Int, height: Int) {
    view.layoutParams.apply {
        this.width = width
        this.height = height
    }
    view.scaleType = ImageView.ScaleType.CENTER_CROP
}

@BindingAdapter("app:isFav")
fun isSongFav(view: ImageButton, isFav: Boolean) {
    if (isFav) {
        view.setImageDrawable(getDrawable(view.context, R.drawable.ic_favorite))
    } else {
        view.setImageDrawable(getDrawable(view.context, R.drawable.ic_no_favorite))
    }
}

@BindingAdapter("app:playState")
fun setPlayState(view: ImageView, state: Int) {
    if (state == STATE_PLAYING) {
        view.setImageResource(R.drawable.play_to_pause)
    } else {
        view.setImageResource(R.drawable.pause_to_play)
    }
}

@BindingAdapter("app:selectedSongs")
fun setTextTitle(view: TextView, selectedSongs: MutableList<Song>) {
    if (selectedSongs.size == 0) {
        view.setText(R.string.select_tracks)
    } else {
        view.text = "${selectedSongs.size}"
    }
}

@BindingAdapter("app:type")
fun setTextByType(view: TextView, type: String) {
    view.apply {
        text = when (type) {
            ARTIST_TYPE -> context.getString(R.string.artist)
            ALBUM_TYPE -> context.getString(R.string.albums)
            FOLDER_TYPE -> context.getString(R.string.folders)
            else -> ""
        }
    }
}

@BindingAdapter("app:title", "app:detail", requireAll = false)
fun setTextTitle(view: TextView, favorite: Favorite, detail: Boolean = false) {
    view.apply {
        text = if (favorite.type == FAVORITE_TYPE) {
            if (!detail) setMargins(top = GeneralUtils.dip2px(context!!, 29))
            context.getString(R.string.favorite_music)
        } else {
            favorite.title
        }
    }
}

@BindingAdapter("app:type", "app:count")
fun setCount(view: TextView, type: String, count: Int) {
    view.text = view.resources.getQuantityString(
        if (type == ARTIST_TYPE) {
            R.plurals.number_of_albums
        } else {
            R.plurals.number_of_songs
        },
        count,
        count
    )
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:album")
fun fixArtistLength(view: TextView, album: Album) {
    val maxSize = if (GeneralUtils.getRotation(view.context) == GeneralUtils.VERTICAL) 15 else 10
    album.apply {
        view.text = "${if (artist.length > maxSize) {
            artist.substring(0, maxSize)
        } else {
            artist
        }} ${view.resources.getString(R.string.separator)} ${view.resources.getQuantityString(
            R.plurals.number_of_songs,
            songCount,
            songCount
        )}"
    }
}

@BindingAdapter("app:clipToOutline")
fun setClipToOutline(view: View, clipToOutline: Boolean) {
    view.clipToOutline = clipToOutline
}

@BindingAdapter("app:textUnderline")
fun textUnderline(view: TextView, textUnderline: Boolean) {
    if (textUnderline)
        view.text = Html.fromHtml("<u>${view.text}</u>", Html.FROM_HTML_MODE_LEGACY)
}


@BindingAdapter("app:visible", "app:animate", requireAll = false)
fun setVisibility(view: View, show: Boolean = true, animate: Boolean = true) {
    if (animate) {
        please(100) {
            animate(view) {
                if (show) scale(1f, 1f) else scale(0f, 0f)
            }
        }.start()
    } else {
        view.toggleShow(show)
    }
}