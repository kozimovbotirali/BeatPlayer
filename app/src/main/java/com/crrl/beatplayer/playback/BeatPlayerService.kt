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

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.MediaId
import com.crrl.beatplayer.models.MediaId.Companion.CALLER_OTHER
import com.crrl.beatplayer.models.MediaId.Companion.CALLER_SELF
import com.crrl.beatplayer.models.QueueInfo
import com.crrl.beatplayer.notifications.Notifications
import com.crrl.beatplayer.playback.players.BeatPlayer
import com.crrl.beatplayer.playback.receivers.BecomingNoisyReceiver
import com.crrl.beatplayer.repository.*
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.APP_PACKAGE_NAME
import com.crrl.beatplayer.utils.BeatConstants.NEXT
import com.crrl.beatplayer.utils.BeatConstants.NOTIFICATION_ID
import com.crrl.beatplayer.utils.BeatConstants.PLAY_PAUSE
import com.crrl.beatplayer.utils.BeatConstants.PREVIOUS
import com.crrl.beatplayer.utils.SettingsUtility
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class BeatPlayerService : MediaBrowserServiceCompat(), KoinComponent {

    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    private val beatPlayer by inject<BeatPlayer>()
    private val notifications by inject<Notifications>()
    private val songsRepository by inject<SongsRepository>()
    private val albumsRepository by inject<AlbumsRepository>()
    private val foldersRepository by inject<FoldersRepository>()
    private val favoritesRepository by inject<FavoritesRepository>()
    private val playlistRepository by inject<PlaylistRepository>()
    private val settingsUtility by inject<SettingsUtility>()

    override fun onCreate() {
        super.onCreate()

        beatPlayer.setData()

        sessionToken = beatPlayer.getSession().sessionToken
        becomingNoisyReceiver = BecomingNoisyReceiver(this, sessionToken!!)

        beatPlayer.onPlayingState { isPlaying ->
            if (isPlaying) {
                startForeground(NOTIFICATION_ID, notifications.buildNotification(getSession()))
                becomingNoisyReceiver.register()
            } else {
                becomingNoisyReceiver.unregister()
                stopForeground(false)
                saveCurrentData()
            }

            if (getSession().controller.playbackState.state != 0)
                notifications.updateNotification(getSession())
        }

        beatPlayer.onCompletion {
            notifications.updateNotification(getSession())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }

        val mediaSession = beatPlayer.getSession()
        val controller = mediaSession.controller

        when (intent.action) {
            PLAY_PAUSE -> {
                controller.playbackState?.let { playbackState ->
                    when {
                        playbackState.isPlaying -> controller.transportControls.pause()
                        playbackState.isPlayEnabled -> controller.transportControls.play()
                    }
                }
            }
            NEXT -> {
                controller.transportControls.skipToNext()
            }
            PREVIOUS -> {
                controller.transportControls.skipToPrevious()
            }
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_STICKY
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        GlobalScope.launch {
            val itemList = withContext(IO) {
                loadChildren(parentId)
            }
            result.sendResult(itemList)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        val caller = if (clientPackageName == APP_PACKAGE_NAME) {
            CALLER_SELF
        } else {
            CALLER_OTHER
        }
        return BrowserRoot(MediaId("-1", null, caller).toString(), null)
    }

    override fun onDestroy() {
        saveCurrentData()
        beatPlayer.release()
        super.onDestroy()
    }

    private fun saveCurrentData() {
        GlobalScope.launch(IO) {
            val mediaSession = beatPlayer.getSession()
            val controller = mediaSession.controller
            if (controller == null ||
                controller.playbackState == null ||
                controller.playbackState.state == STATE_NONE
            ) {
                return@launch
            }

            val queueData = QueueInfo(
                id = controller.metadata?.getString(METADATA_KEY_MEDIA_ID)?.toLong() ?: 0,
                seekPos = controller.playbackState?.position ?: 0,
                repeatMode = controller.repeatMode,
                shuffleMode = controller.shuffleMode,
                state = controller.playbackState?.state ?: STATE_NONE,
                name = controller.queueTitle?.toString() ?: getString(R.string.all_songs)
            )
            settingsUtility.currentQueueList =
                Gson().toJson(mediaSession.controller.queue.toIdList())
            settingsUtility.currentQueueInfo = Gson().toJson(queueData)
        }
    }

    private fun loadChildren(parentId: String): MutableList<MediaBrowserCompat.MediaItem> {
        val list = mutableListOf<MediaBrowserCompat.MediaItem>()
        val mediaId = parentId.toMediaId()

        when (mediaId.type) {
            BeatConstants.SONG_TYPE -> GlobalScope.launch {
                list.addAll(songsRepository.loadSongs().toMediaItemList())
            }
            BeatConstants.ALBUM_TYPE -> GlobalScope.launch {
                list.addAll(
                    albumsRepository.getSongsForAlbum(mediaId.caller?.toLong() ?: 0)
                        .toMediaItemList()
                )
            }
            BeatConstants.PLAY_LIST_TYPE -> GlobalScope.launch {
                list.addAll(
                    playlistRepository.getSongsInPlaylist(mediaId.caller?.toLong() ?: 0)
                        .toMediaItemList()
                )
            }
            BeatConstants.FOLDER_TYPE -> GlobalScope.launch {
                list.addAll(
                    foldersRepository.getSongsForIds(mediaId.caller ?: "").toMediaItemList()
                )
            }
            BeatConstants.FAVORITE_TYPE -> GlobalScope.launch {
                list.addAll(
                    favoritesRepository.getSongsForFavorite(mediaId.caller?.toLong() ?: 0)
                        .toMediaItemList()
                )
            }
        }
        return list
    }
}