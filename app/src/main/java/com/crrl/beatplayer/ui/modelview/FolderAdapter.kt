package com.crrl.beatplayer.ui.modelview

import android.content.ContentUris
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FolderItemBinding
import com.crrl.beatplayer.extensions.dataChanged
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.utils.PlayerConstants

class FolderAdapter(private val context: Context?) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    var folderList: MutableList<Folder> = mutableListOf()
    var itemClickListener: ItemClickListener<Folder>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = parent.inflateWithBinding<FolderItemBinding>(R.layout.folder_item)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    fun updateDataSet(newList: List<Folder>) {
        dataChanged(newList)
    }

    private fun getItem(position: Int) = folderList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: FolderItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(folder: Folder) {
            binding.apply {
                this.folder = folder
                itemMenu.setOnClickListener(this@ViewHolder)
                cover.clipToOutline = true
                val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, folder.albumId)
                Glide.with(context!!)
                    .load(uri)
                    .placeholder(R.drawable.ic_empty_cover)
                    .error(R.drawable.ic_empty_cover)
                    .into(binding.cover)
                executePendingBindings()
            }
        }

        override fun onClick(view: View) {
            if (itemClickListener != null)
                when (view.id) {
                    R.id.item_menu -> itemClickListener!!.onPopupMenuClick(
                        view,
                        adapterPosition,
                        getItem(adapterPosition)
                    )
                    R.id.container -> itemClickListener!!.onItemClick(
                        view,
                        adapterPosition,
                        getItem(adapterPosition)
                    )
                }
        }
    }
}