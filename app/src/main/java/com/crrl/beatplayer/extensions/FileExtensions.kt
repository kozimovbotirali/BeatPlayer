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
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.GeneralUtils.getStoragePaths
import timber.log.Timber
import java.io.File

private const val INTERNAL_STORAGE = "/Internal Storage"
private const val EXTERNAL_STORAGE = "/SD Card"
private const val MEGA_BYTES_SIZE = 1048576

fun File.fixedPath(context: Context): String {
    val storagePaths = getStoragePaths(context)
    return if (path.contains(storagePaths[0])) {
        path.replace(storagePaths[0], INTERNAL_STORAGE)
    } else {
        path.replace(storagePaths[1], EXTERNAL_STORAGE)
    }
}

fun File.fixedName(context: Context): String{
    return File(fixedPath(context)).name
}

fun File.sizeMB(): String? {
    return "${length() / MEGA_BYTES_SIZE} MB"
}