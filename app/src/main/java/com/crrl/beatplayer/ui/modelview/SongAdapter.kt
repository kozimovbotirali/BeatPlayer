package com.crrl.beatplayer.ui.modelview

import android.content.ContentUris
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.SongItemBinding
import com.crrl.beatplayer.databinding.SongItemHeaderBinding
import com.crrl.beatplayer.extensions.dataChanged
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.PlayerConstants

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

class SongAdapter(private val context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var songList: MutableList<Song> = mutableListOf()
    var showHeader: Boolean = false
    var isPlaylist: Boolean = false
    var itemClickListener: ItemClickListener<Song>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<SongItemHeaderBinding>(R.layout.song_item_header)
                ViewHolderSongHeader(viewBinding)
            }
            ITEM_TYPE -> {
                val viewBinding = parent.inflateWithBinding<SongItemBinding>(R.layout.song_item)
                ViewHolderSong(viewBinding)
            }
            else -> {
                val viewBinding = parent.inflateWithBinding<SongItemBinding>(R.layout.song_item)
                ViewHolderSong(viewBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentSong = if (!songList.isNullOrEmpty()) getItem(position) else null
        when (getItemViewType(position)) {
            HEADER_TYPE -> {
                (holder as ViewHolderSongHeader).bind(songList.size, isPlaylist)
            }
            ITEM_TYPE -> {
                (holder as ViewHolderSong).bind(currentSong!!)
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
        if (!isPlaylist) {
            Thread {
                this.songList = songList.toMutableList()
                (context as AppCompatActivity).runOnUiThread {
                    notifyDataSetChanged()
                }
            }.start()
        } else {
            dataChanged(songList)
        }
    }

    inner class ViewHolderSong(private val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(song: Song) {
            binding.apply {
                this.song = song
                cover.clipToOutline = true
                container.setOnClickListener(this@ViewHolderSong)
                itemMenu.setOnClickListener(this@ViewHolderSong)
                val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, song.albumId)
                Glide.with(context!!)
                    .load(uri)
                    .placeholder(R.drawable.ic_empty_cover)
                    .error(R.drawable.ic_empty_cover)
                    .into(cover)
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

    inner class ViewHolderSongHeader(private val binding: SongItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(songCount: Int, isPlaylist: Boolean) {
            binding.apply {
                shuffleSong.setOnClickListener(this@ViewHolderSongHeader)
                sortSong.setOnClickListener(this@ViewHolderSongHeader)
                playAllSong.setOnClickListener(this@ViewHolderSongHeader)
                this.songCount = songCount
                this.isPlaylist = isPlaylist
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.shuffle_song -> itemClickListener!!.onShuffleClick(view)
                    R.id.sort_song -> itemClickListener!!.onSortClick(view)
                    R.id.play_all_song -> itemClickListener!!.onPlayAllClick(view)
                }
        }

    }
}
