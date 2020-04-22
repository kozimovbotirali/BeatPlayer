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

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FavoriteItemBinding
import com.crrl.beatplayer.extensions.deepEquals
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.utils.GeneralUtils.dip2px
import com.crrl.beatplayer.utils.GeneralUtils.screenWidth

class FavoriteAdapter(private val context: Context?) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolderFavorite>() {

    private var favoriteList: MutableList<Favorite> = mutableListOf()
    private var lastClick = 0L
    var itemClickListener: ItemClickListener<Favorite>? = null
    var spanCount: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.ViewHolderFavorite {
        val viewBinding = parent.inflateWithBinding<FavoriteItemBinding>(R.layout.favorite_item)
        return ViewHolderFavorite(viewBinding)
    }

    override fun getItemCount() = favoriteList.size

    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolderFavorite, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateDataSet(newList: List<Favorite>) {
        if (!favoriteList.deepEquals(newList)) {
            favoriteList = newList.toMutableList()
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): Favorite {
        return favoriteList[position]
    }

    inner class ViewHolderFavorite(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(artist: Favorite) {
            binding.apply {
                this.favorite = artist
                executePendingBindings()

                showDetails.setOnClickListener(this@ViewHolderFavorite)
                container.layoutParams.apply {
                    height = screenWidth / spanCount + dip2px(context!!, 22)
                    width = screenWidth / spanCount - dip2px(context, 6)
                }
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
                getItem(adapterPosition)
            )
        }
    }
}