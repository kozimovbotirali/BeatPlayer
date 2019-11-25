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

package com.crrl.beatplayer.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.repository.AlbumsRepository

class AlbumViewModel(private val context: Context?) : ViewModel() {

    private val albums: MutableLiveData<List<Album>>? = MutableLiveData()

    init {
        Thread{
            albums!!.postValue(AlbumsRepository.getInstance(context)!!.getAlbums())
        }.start()
    }

    fun update() {
        Thread {
            albums!!.postValue(AlbumsRepository.getInstance(context)!!.getAlbums())
        }.start()
    }


    fun getAlbums(): LiveData<List<Album>>? {
        return albums
    }
}
