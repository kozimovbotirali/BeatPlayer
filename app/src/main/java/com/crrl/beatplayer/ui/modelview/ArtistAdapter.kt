package com.crrl.beatplayer.ui.modelview

import android.content.ContentUris
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ArtistItemBinding
import com.crrl.beatplayer.databinding.ArtistItemHeaderBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.repository.ArtistRepository
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants

private const val HEADER_TYPE = 0
private const val ITEM_TYPE = 1

class ArtistAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var artistList: List<Artist> = emptyList()
    var showHeader: Boolean = false
    var itemClickListener: ItemClickListener<Artist>? = null
    var spanCount: Int = 0
    private var lastClick = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_TYPE -> {
                val viewBinding =
                    parent.inflateWithBinding<ArtistItemHeaderBinding>(R.layout.artist_item_header)
                ViewHolderAlbumHeader(viewBinding)
            }
            ITEM_TYPE -> {
                val viewBinding = parent.inflateWithBinding<ArtistItemBinding>(R.layout.artist_item)
                ViewHolderArtist(viewBinding)
            }
            else -> {
                val viewBinding = parent.inflateWithBinding<ArtistItemBinding>(R.layout.artist_item)
                ViewHolderArtist(viewBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentSong = if (!artistList.isNullOrEmpty()) getItem(position) else null
        when (getItemViewType(position)) {
            HEADER_TYPE -> {
                (holder as ViewHolderAlbumHeader).bind(artistList.size)
            }
            ITEM_TYPE -> {
                (holder as ViewHolderArtist).bind(currentSong!!)
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
        artistList.size + 1
    } else {
        artistList.size
    }

    fun getItem(position: Int): Artist? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                artistList[position - 1]
            }
        } else {
            artistList[position]
        }
    }

    fun updateDataSet(artistList: List<Artist>) {
        Thread {
            this.artistList = artistList
            (context as AppCompatActivity).runOnUiThread {
                notifyDataSetChanged()
            }
        }.start()
    }

    inner class ViewHolderArtist(private val binding: ArtistItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(artist: Artist) {
            binding.apply {
                this.artist = artist
                cover.clipToOutline = true
                Thread(Runnable {
                    val uri = ContentUris.withAppendedId(
                        PlayerConstants.ARTWORK_URI,
                        ArtistRepository.getInstance(context!!)!!.getAlbumsForArtist(artist.id)[0].id
                    )
                    (context as AppCompatActivity).runOnUiThread {
                        Glide.with(context)
                            .load(uri)
                            .placeholder(R.drawable.ic_empty_cover)
                            .error(R.drawable.ic_empty_cover)
                            .into(cover)
                    }
                }).start()
                showDetails.setOnClickListener(this@ViewHolderArtist)
                container.layoutParams.height =
                    GeneralUtils.screenWidth / spanCount + GeneralUtils.dip2px(context!!, 42)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (SystemClock.elapsedRealtime() - lastClick < 300) {
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

    inner class ViewHolderAlbumHeader(private val binding: ArtistItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(artistCount: Int) {
            binding.apply {
                this.artistCount = artistCount
                sortArtist.setOnClickListener(this@ViewHolderAlbumHeader)
                playAllArtist.setOnClickListener(this@ViewHolderAlbumHeader)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.play_all_artist -> itemClickListener!!.onPlayAllClick(view)
                    R.id.sort_artist -> itemClickListener!!.onSortClick(view)
                }
        }
    }
}