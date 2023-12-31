package com.dreamfactory.brain_tumor_detection.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

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

@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {

    var imageUri: Uri? = null

    private val _result: MutableStateFlow<String> = MutableStateFlow("")

    //get image from gallery or camera
    init {

    }

    fun getCurrentTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formatter.format(currentDateTime)
    }
}