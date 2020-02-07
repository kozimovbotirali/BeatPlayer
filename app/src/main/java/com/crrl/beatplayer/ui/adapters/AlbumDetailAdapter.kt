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

package com.crrl.beatplayer.ui.adapters

import android.content.ContentUris
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.AlbumDetailItemBinding
import com.crrl.beatplayer.databinding.AlbumDetailItemHeaderBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

class AlbumSongAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var songList: List<Song> = emptyList()
    var showHeader: Boolean = false
    var itemClickListener: ItemClickListener<Song>? = null
    var album = Album()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<AlbumDetailItemHeaderBinding>(R.layout.album_detail_item_header)
                ViewHolderAlbumSongHeader(viewBinding)
            }
            ITEM_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<AlbumDetailItemBinding>(R.layout.album_detail_item)
                ViewHolderAlbumSong(viewBinding)
            }
            else -> {
                val viewBinding =
                    parent.inflateWithBinding<AlbumDetailItemBinding>(R.layout.album_detail_item)
                ViewHolderAlbumSong(viewBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentSong = if (!songList.isNullOrEmpty()) getItem(position) else null
        when (getItemViewType(position)) {
            HEADER_TYPE -> {
                (holder as ViewHolderAlbumSongHeader).bind()
            }
            ITEM_TYPE -> {
                (holder as ViewHolderAlbumSong).bind(currentSong!!)
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
        songList.size + 1
    } else {
        songList.size
    }

    fun getItem(position: Int): Song? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                songList[position - 1]
            }
        } else {
            songList[position]
        }
    }

    fun updateDataSet(songList: List<Song>) {
        Thread {
            this.songList = songList
            (context as AppCompatActivity).runOnUiThread {
                notifyDataSetChanged()
            }
        }.start()
    }

    inner class ViewHolderAlbumSong(private val binding: AlbumDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(song: Song) {
            binding.apply {
                this.song = song
                container.setOnClickListener(this@ViewHolderAlbumSong)
                itemMenu.setOnClickListener(this@ViewHolderAlbumSong)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.item_menu -> itemClickListener!!.onPopupMenuClick(
                        view,
                        adapterPosition,
                        getItem(adapterPosition)!!
                    )
                    R.id.container -> itemClickListener!!.onItemClick(
                        view,
                        adapterPosition,
                        getItem(adapterPosition)!!
                    )
                }
        }
    }

    inner class ViewHolderAlbumSongHeader(private val binding: AlbumDetailItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind() {
            binding.apply {
                shuffleAlbumSong.setOnClickListener(this@ViewHolderAlbumSongHeader)
                playAllAlbumSong.setOnClickListener(this@ViewHolderAlbumSongHeader)
                totalDuration = GeneralUtils.getTotalTime(songList).toInt()
                this.album = this@AlbumSongAdapter.album
                cover.clipToOutline = true
                val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, album!!.id)
                Glide.with(context!!)
                    .load(uri)
                    .placeholder(R.drawable.song_cover_frame)
                    .error(R.drawable.ic_empty_cover)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(cover)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.shuffle_album_song -> itemClickListener!!.onShuffleClick(view)
                    R.id.play_all_album_song -> itemClickListener!!.onPlayAllClick(view)
                }
        }
    }
}

