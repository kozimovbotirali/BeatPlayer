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

package com.crrl.beatplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.crrl.beatplayer.models.Song;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import static com.crrl.beatplayer.utils.BeatConstants.AUTO_THEME;
import static com.crrl.beatplayer.utils.BeatConstants.DID_STOP_KEY;
import static com.crrl.beatplayer.utils.BeatConstants.QUEUE_INFO_KEY;
import static com.crrl.beatplayer.utils.BeatConstants.QUEUE_LIST_KEY;
import static com.crrl.beatplayer.utils.BeatConstants.SONG_KEY;

public final class SettingsUtility {

    private static final String SHARED_PREFERENCES_FILE_NAME = "configs";
    private static final String SONG_SORT_ORDER_KEY = "song_sort_order";
    private static final String ALBUM_SORT_ORDER_KEY = "album_sort_order";
    private static final String ALBUM_SONG_SORT_ORDER_KEY = "album_song_sort_order";
    private static final String ARTIST_SORT_ORDER_KEY = "album_song_sort_order";
    private static final String LAST_OPTION_SELECTED_KEY = "last_option_selected";
    private static final String CURRENT_THEME_KEY = "current_theme";

    private SharedPreferences sPreferences;

    public SettingsUtility(@NotNull Context context) {
        sPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public int getStartPageIndexSelected() {
        return sPreferences.getInt(LAST_OPTION_SELECTED_KEY, 2);
    }

    public void setStartPageIndexSelected(int value) {
        setPreference(LAST_OPTION_SELECTED_KEY, value);
    }

    public String getSongSortOrder() {
        return sPreferences.getString(SONG_SORT_ORDER_KEY, SortModes.SongModes.SONG_A_Z);
    }

    public void setSongSortOrder(String value) {
        setPreference(SONG_SORT_ORDER_KEY, value);
    }

    public String getAlbumSortOrder() {
        return sPreferences.getString(ALBUM_SORT_ORDER_KEY, SortModes.AlbumModes.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(String value) {
        setPreference(ALBUM_SORT_ORDER_KEY, value);
    }

    public String getCurrentTheme() {
        return sPreferences.getString(CURRENT_THEME_KEY, AUTO_THEME);
    }

    public void setCurrentTheme(String value) {
        setPreference(CURRENT_THEME_KEY, value);
    }

    public String getAlbumSongSortOrder() {
        return sPreferences.getString(ALBUM_SONG_SORT_ORDER_KEY, SortModes.SongModes.SONG_TRACK);
    }

    public void setAlbumSongSortOrder(String value) {
        setPreference(ALBUM_SONG_SORT_ORDER_KEY, value);
    }

    public String getCurrentSongSelected() {
        return sPreferences.getString(SONG_KEY, new Gson().toJson(new Song()));
    }

    public void setCurrentSongSelected(String value) {
        setPreference(BeatConstants.SONG_KEY, value);
    }

    public String getCurrentQueueInfo() {
        return sPreferences.getString(QUEUE_INFO_KEY, null);
    }

    public void setCurrentQueueInfo(String value) {
        setPreference(QUEUE_INFO_KEY, value);
    }

    public String getCurrentQueueList() {
        return sPreferences.getString(QUEUE_LIST_KEY, null);
    }

    public void setCurrentQueueList(String value) {
        setPreference(QUEUE_LIST_KEY, value);
    }

    public String getArtistSortOrder() {
        return sPreferences.getString(ARTIST_SORT_ORDER_KEY, SortModes.ArtistModes.ARTIST_A_Z);
    }

    public void setArtistSortOrder(String value) {
        setPreference(ARTIST_SORT_ORDER_KEY, value);
    }

    public boolean getDidStop() {
        return sPreferences.getBoolean(DID_STOP_KEY, false);
    }

    public void setDidStop(boolean value) {
        setPreference(DID_STOP_KEY, value);
    }

    private void setPreference(String key, String value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void setPreference(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void setPreference(String key, boolean state) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(key, state);
        editor.apply();
    }
}
