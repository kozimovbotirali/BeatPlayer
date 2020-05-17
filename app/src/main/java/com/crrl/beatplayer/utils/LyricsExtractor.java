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

import android.net.Uri;

import com.crrl.beatplayer.models.MediaItemData;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LyricsExtractor {

    public static String getLyric(MediaItemData mediaItemData) {
        String lyrics = getEmbeddedLyrics(mediaItemData);
        return lyrics == null ? getOnlineLyrics(mediaItemData) : lyrics;
    }

    private static String getEmbeddedLyrics(MediaItemData mediaItemData) {
        StringBuilder lyrics = new StringBuilder();
        Uri uri = GeneralUtils.INSTANCE.getSongUri(mediaItemData.getId());

        File file = new File(Objects.requireNonNull(uri.getPath()));
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            lyrics.append(audioFile.getTag().getFirst(FieldKey.LYRICS));
        } catch (CannotReadException | IOException | TagException
                | ReadOnlyFileException | InvalidAudioFrameException ignored) {
        }
        return lyrics.toString().isEmpty() ? null : lyrics.toString();
    }

    private static String getOnlineLyrics(MediaItemData mediaItemData) {
        LyricsClient client = new LyricsClient();
        Lyrics lyrics = null;
        try {
            lyrics = client.getLyrics(mediaItemData.getTitle() + " - " + mediaItemData.getArtist().replace(";", ",") + " " + mediaItemData.getAlbum()).get();
            /*if (lyrics == null) {
                lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().split("[;,]")[0] + " " + song.getAlbum()).get();
            }
            if (lyrics == null) {
                lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().replace(";", ",")).get();
            }
            if (lyrics == null) {
                lyrics = client.getLyrics(song.getTitle() + " - " + song.getArtist().split("[;,]")[0]).get();
            }*/
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return lyrics != null ? lyrics.getContent() : null;
    }
}
