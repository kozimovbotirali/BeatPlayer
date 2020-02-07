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

package com.crrl.beatplayer.playback

import com.crrl.beatplayer.models.Song

interface MediaControls{
    fun play()
    fun pause()
    fun stop()
    fun next(currentSong: Song) : Song
    fun previous(currentSong: Song) : Song
}

class MusicService : MediaControls{

    private var songList: List<Song> = emptyList()

    override fun play() {

    }

    override fun pause() {

    }

    override fun stop() {

    }

    // Calculating the next song based on the current list and song
    override fun next(currentSong: Song): Song {
        val currentIndex = songList.indexOf(currentSong)
        return if (currentIndex == songList.size - 1){
            songList[0]
        }else{
            songList[currentIndex+1]
        }
    }

    // Calculating the previous song based on the current list and song
    override fun previous(currentSong: Song): Song {
        val currentIndex = songList.indexOf(currentSong)
        return if (currentIndex == 0){
            songList[songList.size - 1]
        }else{
            songList[currentIndex-1]
        }
    }

    fun updateData(list: List<Song>) {
        songList = list
    }
}