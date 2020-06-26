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

package com.crrl.beatplayer.extensions

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.QUEUE_INFO_KEY
import com.crrl.beatplayer.utils.BeatConstants.SEEK_TO_POS
import com.crrl.beatplayer.utils.BeatConstants.SONG_LIST_NAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException

inline fun <reified T> Gson.fromJson(json: String) =
    this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!

fun MediaSessionCompat.position(): Long {
    return controller.playbackState.position
}

fun MediaSessionCompat.isPlaying(): Boolean {
    return controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING
}

inline val MediaSessionCompat.repeatMode
    get() = controller.repeatMode

inline val MediaSessionCompat.shuffleMode
    get() = controller.shuffleMode

inline val PlaybackStateCompat.isPrepared
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING) ||
            (state == PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.isPlaying
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING)

inline val PlaybackStateCompat.isPlayEnabled
    get() = (actions and PlaybackStateCompat.ACTION_PLAY != 0L) ||
            ((actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L) &&
                    (state == PlaybackStateCompat.STATE_PAUSED))

inline val MediaMetadataCompat.id: String get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.title: String get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.artist: String get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.duration get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

inline val MediaMetadataCompat.album: String get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

fun getExtraBundle(queue: LongArray, title: String): Bundle? {
    return getExtraBundle(queue, title, 0)
}

fun getExtraBundle(queue: LongArray, title: String, seekTo: Int?): Bundle? {
    val bundle = Bundle()
    bundle.putLongArray(QUEUE_INFO_KEY, queue)
    bundle.putString(SONG_LIST_NAME, title)
    if (seekTo != null)
        bundle.putInt(SEEK_TO_POS, seekTo)
    else bundle.putInt(SEEK_TO_POS, 0)
    return bundle
}

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

fun Uri.toFileDescriptor(context: Context): ParcelFileDescriptor? {
    return try {
        context.contentResolver.openFileDescriptor(this, BeatConstants.READ_ONLY_MODE, null) ?: null
    } catch (ex: FileNotFoundException) {
        null
    }
}

operator fun Bundle.plus(other: Bundle) = this.apply { putAll(other) }