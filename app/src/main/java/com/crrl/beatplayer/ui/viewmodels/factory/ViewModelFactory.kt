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

package com.crrl.beatplayer.ui.viewmodels.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.viewmodels.*

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val context: Context? = null, val activity: MainActivity? = null) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SongViewModel::class.java) -> SongViewModel(context) as T
            modelClass.isAssignableFrom(AlbumViewModel::class.java) -> AlbumViewModel(context) as T
            modelClass.isAssignableFrom(FolderViewModel::class.java) -> FolderViewModel(context) as T
            modelClass.isAssignableFrom(SongDetailViewModel::class.java) -> SongDetailViewModel(activity!!) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(activity) as T
            else -> throw IllegalArgumentException("ViewModel class not supported.")
        }
    }
}