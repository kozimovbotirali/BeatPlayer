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

package com.crrl.beatplayer.ui.viewmodels

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.FavoritesRepository

class FavoriteViewModel(private val repository: FavoritesRepository) : ViewModel() {

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()
    private val favorites: MutableLiveData<List<Favorite>> = MutableLiveData()

    fun songListFavorite(idFavorites: Long): LiveData<List<Song>> {
        update(idFavorites)
        return songs
    }

    fun getFavorites(): LiveData<List<Favorite>> {
        update()
        return favorites
    }

    fun update(idFavorites: Long) {
        Thread {
            songs.postValue(repository.getSongsForFavorite(idFavorites))
        }.start()
    }

    fun update() {
        Thread {
            favorites.postValue(
                try {
                    repository.getFavorites()
                } catch (ex: SQLiteException) {
                    null
                }
            )
        }.start()
    }
}