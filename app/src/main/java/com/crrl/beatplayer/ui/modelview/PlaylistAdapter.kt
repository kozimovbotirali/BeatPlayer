package com.crrl.beatplayer.ui.modelview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.PlaylistItemBinding
import com.crrl.beatplayer.extensions.dataChanged
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    var itemClickListener: ItemClickListener<Playlist>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = parent.inflateWithBinding<PlaylistItemBinding>(R.layout.playlist_item)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updateDataSet(newList: List<Playlist>): Boolean {
        return dataChanged(newList)
    }

    private fun getItem(position: Int) = playlists[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(playlist: Playlist) {
            binding.apply {
                this.playlist = playlist
                itemMenu.setOnClickListener(this@ViewHolder)
                container.setOnClickListener(this@ViewHolder)
                executePendingBindings()
            }
        }

        override fun onClick(view: View?) {
            when (view!!.id) {
                R.id.container -> if (itemClickListener != null) itemClickListener!!.onItemClick(
                    view,
                    layoutPosition,
                    getItem(layoutPosition)
                )
                R.id.item_menu -> if (itemClickListener != null) itemClickListener!!.onPopupMenuClick(
                    view,
                    layoutPosition,
                    getItem(layoutPosition)
                )
            }
        }
    }
}