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

import android.os.Environment
import android.util.Log
import java.io.File

private const val INTERNAL_STORAGE = "/Internal Storage"
private const val EXTERNAL_STORAGE = "/SD Card"

fun File?.fixedPath(): String {
    try {
        val fixedPath =
            StringBuilder(if (Environment.isExternalStorageEmulated(this!!)) INTERNAL_STORAGE else EXTERNAL_STORAGE)
        val parts = path.split("/")
        for ((i, part) in parts.withIndex()) {
            if (i > if (fixedPath.contains(EXTERNAL_STORAGE)) 2 else 3) {
                fixedPath.append("/$part")
            }
        }
        return fixedPath.toString()
    } catch (ex: IllegalArgumentException) {
        Log.println(Log.ERROR, "IllegalArgumentException", ex.message!!)
    }
    return this?.name!!
}

fun File?.fixedName(): String {
    try {
        val fixedPath =
            if (Environment.isExternalStorageEmulated(this!!)) INTERNAL_STORAGE else EXTERNAL_STORAGE
        val parts = path.split("/")
        return if ((parts.size == 3 && fixedPath == EXTERNAL_STORAGE) || (parts.size == 4 && fixedPath == INTERNAL_STORAGE)) fixedPath.substring(
            fixedPath.indexOf("/") + 1
        ) else parts[parts.size - 1]
    } catch (ex: IllegalArgumentException) {
        Log.println(Log.ERROR, "IllegalArgumentException", ex.message!!)
    }
    return this?.name!!
}