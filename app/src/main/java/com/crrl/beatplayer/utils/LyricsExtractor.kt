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
package com.crrl.beatplayer.utils

import com.crrl.beatplayer.extensions.fixName
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.utils.GeneralUtils.getSongUri
import com.jagrosh.jlyrics.Lyrics
import com.jagrosh.jlyrics.LyricsClient
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException

object LyricsExtractor {
    fun getLyric(mediaItemData: MediaItemData): String? {
        val lyrics = getEmbeddedLyrics(mediaItemData)
        return lyrics ?: getOnlineLyrics(mediaItemData)
    }

    private fun getEmbeddedLyrics(mediaItemData: MediaItemData): String? {
        val lyrics = StringBuilder()
        val uri = getSongUri(mediaItemData.id)
        val file = File(Objects.requireNonNull(uri.path))
        try {
            val audioFile = AudioFileIO.read(file)
            lyrics.append(audioFile.tag.getFirst(FieldKey.LYRICS))
        } catch (ex: CannotReadException) {
            Timber.e(ex)
        } catch (ex: IOException) {
            Timber.e(ex)
        } catch (ex: TagException) {
            Timber.e(ex)
        } catch (ex: ReadOnlyFileException) {
            Timber.e(ex)
        } catch (ex: InvalidAudioFrameException) {
            Timber.e(ex)
        }
        return if (lyrics.toString().isEmpty()) null else lyrics.toString()
    }

    private fun getOnlineLyrics(mediaItemData: MediaItemData): String? {
        val client = LyricsClient()
        var lyrics: Lyrics? = null
        try {
            lyrics = client.getLyrics(
                mediaItemData.title.fixName() + " by " + mediaItemData.artist
            ).get()
        } catch (ex: InterruptedException) {
            Timber.e(ex)
        } catch (ex: ExecutionException) {
            Timber.e(ex)
        }
        return lyrics?.content
    }
}