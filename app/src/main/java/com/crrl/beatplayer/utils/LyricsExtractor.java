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

import com.crrl.beatplayer.models.Song;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;

import java.util.concurrent.ExecutionException;

public class LyricsExtractor {

    public static String getLyric(Song song) {
        LyricsClient client = new LyricsClient();
        Lyrics lyrics = null;
        try {
            lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().replace(";", ",") + " " + song.getAlbum()).get();
            if (lyrics == null) {
                lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().split("[;,]")[0] + " " + song.getAlbum()).get();
            }
            if (lyrics == null) {
                lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().replace(";", ",")).get();
            }
            if (lyrics == null) {
                lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().split("[;,]")[0]).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return lyrics != null ? lyrics.getContent() : null;
    }
}
