package com.dreamfactory.brain_tumor_detection

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.core.net.toFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/*
 * Designed and developed by 2023 huiung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object Utils {

    val classIndexKey = listOf<String>(
        "Astrocitoma T1",
        "Astrocitoma T1C+",
        "Astrocitoma T2",
        "Carcinoma T1",
        "Carcinoma T1C+",
        "Carcinoma T2",
        "Ependimoma T1",
        "Ependimoma T1C+",
        "Ependimoma T2",
        "Ganglioglioma T1",
        "Ganglioglioma T1C+",
        "Ganglioglioma T2",
        "Germinoma T1",
        "Germinoma T1C+",
        "Germinoma T2",
        "Glioblastoma T1",
        "Glioblastoma T1C+",
        "Glioblastoma T2",
        "Granuloma T1",
        "Granuloma T1C+",
        "Granuloma T2",
        "Meduloblastoma T1",
        "Meduloblastoma T1C+",
        "Meduloblastoma T2",
        "Meningioma T1",
        "Meningioma T1C+",
        "Meningioma T2",
        "Neurocitoma T1",
        "Neurocitoma T1C+",
        "Neurocitoma T2",
        "Oligodendroglioma T1",
        "Oligodendroglioma T1C+",
        "Oligodendroglioma T2",
        "Papiloma T1",
        "Papiloma T1C+",
        "Papiloma T2",
        "Schwannoma T1",
        "Schwannoma T1C+",
        "Schwannoma T2",
        "Tuberculoma T1",
        "Tuberculoma T1C+",
        "Tuberculoma T2",
        "_NORMAL T1",
        "_NORMAL T2")

    fun saveBitmapImage(bitmap: Bitmap, context: Context, uriState: MutableState<Uri?>) {
        CoroutineScope(Dispatchers.IO).launch {
            val timestamp = System.currentTimeMillis()

            //Tell the media scanner about the new file so that it is immediately available to the user.
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
                values.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/" + context.getString(R.string.app_name)
                )
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                if (uri != null) {
                    try {
                        val outputStream = context.contentResolver.openOutputStream(uri)
                        if (outputStream != null) {
                            try {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                outputStream.close()
                            } catch (e: Exception) {

                            }
                        }
                        values.put(MediaStore.Images.Media.IS_PENDING, false)
                        context.contentResolver.update(uri, values, null, null)
                        uriState.value = uri
                    } catch (e: Exception) {

                    }
                }
            } else {
                val imageFileFolder = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + '/' + context.getString(R.string.app_name)
                )
                if (!imageFileFolder.exists()) {
                    imageFileFolder.mkdirs()
                }
                val mImageName = "$timestamp.png"
                val imageFile = File(imageFileFolder, mImageName)
                uriState.value = Uri.fromFile(imageFile)
                try {
                    val outputStream: OutputStream = FileOutputStream(imageFile)
                    try {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()
                    } catch (e: Exception) {

                    }
                    values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                } catch (e: Exception) {

                }
            }
        }
    }

    fun shareImageByEmail(uri: Uri, context: Context) {

        // 이메일 앱으로 이미지 공유
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "뇌종양 검출 결과 이미지")
            putExtra(Intent.EXTRA_TEXT, "뇌종양 검출 결과 이미지 입니다.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Intent를 처리하는 Activity를 실행
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }
}