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

package com.crrl.beatplayer.ui.adapters

import android.content.ContentUris
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.AlbumItemBinding
import com.crrl.beatplayer.databinding.AlbumItemHeaderBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

class AlbumAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var albumList: List<Album> = emptyList()
    private var lastClick = 0L

    var showHeader: Boolean = false
    var itemClickListener: ItemClickListener<Album>? = null
    var spanCount: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<AlbumItemHeaderBinding>(R.layout.album_item_header)
                ViewHolderAlbumHeader(viewBinding)
            }
            ITEM_TYPE -> {
                val viewBinding = parent.inflateWithBinding<AlbumItemBinding>(R.layout.album_item)
                ViewHolderAlbum(viewBinding)
            }
            else -> {
                val viewBinding = parent.inflateWithBinding<AlbumItemBinding>(R.layout.album_item)
                ViewHolderAlbum(viewBinding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showHeader && position == 0) {
            HEADER_TYPE
        } else {
            ITEM_TYPE
        }
    }

    override fun getItemCount() = if (showHeader) {
        albumList.size + 1
    } else {
        albumList.size
    }

    fun getItem(position: Int): Album? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                albumList[position - 1]
            }
        } else {
            albumList[position]
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentSong = if (!albumList.isNullOrEmpty()) getItem(position) else null
        when (getItemViewType(position)) {
            HEADER_TYPE -> {
                (holder as ViewHolderAlbumHeader).bind(albumList.size)
            }
            ITEM_TYPE -> {
                (holder as ViewHolderAlbum).bind(currentSong!!)
            }
        }
    }

    fun updateDataSet(albumList: List<Album>) {
        Thread {
            this.albumList = albumList
            (context as AppCompatActivity).runOnUiThread {
                notifyDataSetChanged()
            }
        }.start()
    }

    inner class ViewHolderAlbum(private val binding: AlbumItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(album: Album) {
            binding.apply {
                this.album = album
                width = GeneralUtils.screenWidth / spanCount - GeneralUtils.dip2px(context!!, 22)
                cover.clipToOutline = true
                val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, album.id)
                Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.ic_empty_cover)
                    .error(R.drawable.ic_empty_cover)
                    .into(cover)
                showDetails.setOnClickListener(this@ViewHolderAlbum)
                container.layoutParams.height =
                    GeneralUtils.screenWidth / spanCount + GeneralUtils.dip2px(context, 42)
                container.layoutParams.width =
                    GeneralUtils.screenWidth / spanCount - GeneralUtils.dip2px(context, 6)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (SystemClock.elapsedRealtime() - lastClick < 500) {
                return
            }
            lastClick = SystemClock.elapsedRealtime()
            if (itemClickListener != null) itemClickListener!!.onItemClick(
                view,
                adapterPosition,
                getItem(adapterPosition)!!
            )
        }
    }

    inner class ViewHolderAlbumHeader(private val binding: AlbumItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(albumCount: Int) {
            binding.apply {
                this.albumCount = albumCount
                sortAlbum.setOnClickListener(this@ViewHolderAlbumHeader)
                playAllAlbum.setOnClickListener(this@ViewHolderAlbumHeader)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.play_all_album -> itemClickListener!!.onPlayAllClick(view)
                    R.id.sort_album -> itemClickListener!!.onSortClick(view)
                }
        }
    }
}