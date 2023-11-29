package com.dreamfactory.brain_tumor_detection.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.dreamfactory.brain_tumor_detection.R
import com.dreamfactory.brain_tumor_detection.Utils
import com.dreamfactory.brain_tumor_detection.ml.BrainTumorModel
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectRoute(
    vm: MainViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(stringResource(id = R.string.inspect))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        InspectScreen(innerPadding, vm, navController, onBackPressed)
    }
}

@Composable
fun InspectScreen(
    innerPadding: PaddingValues,
    vm: MainViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleScope = rememberCoroutineScope()
    val captureController = rememberCaptureController()

    val screenHeight = context.resources.displayMetrics.heightPixels
    val maxHeight = with (LocalDensity.current) {
        (screenHeight * 0.65f).toDp()
    }

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .allowHardware(false)
            .build()
    }

    val result = remember { mutableStateOf("" to false) }
    var inspect by remember { mutableStateOf(false) }
    var image by remember { mutableStateOf<Bitmap?>(null) }
    var saveImage by remember { mutableStateOf(false) }
    val uri: MutableState<Uri?> = remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        vm.imageUri?.let {
            Capturable(
                controller = captureController,
                onCaptured = { bitmap, error ->
                    // This is captured bitmap of a content inside Capturable Composable.
                    if (bitmap != null) {
                        // Bitmap is captured successfully. Do something with it!
                        Utils.saveBitmapImage(bitmap.asAndroidBitmap(), context, uri)
                    }

                    if (error != null) {
                        // Error occurred. Handle it!
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = maxHeight),
                            imageLoader = imageLoader,
                            model = vm.imageUri,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            onSuccess = { imageState ->
                                image = Bitmap.createScaledBitmap(
                                    imageState.result.drawable.toBitmap(),
                                    150,
                                    150,
                                    false
                                )
                            },
                        )
                        if (result.value.first != "") {
                            if (!saveImage) {
                                android.os.Handler(Looper.getMainLooper()).post {
                                    captureController.capture()
                                }
                                saveImage = true
                            }
                            Image(
                                painter = if (result.value.second) {
                                    painterResource(id = R.drawable.ic_pass)
                                } else {
                                    painterResource(id = R.drawable.ic_fail)
                                },
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Text(
                        text = result.value.first,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 11.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!inspect && image != null) {
                    Button(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {
                            classifyImage(
                                vm = vm,
                                result = result,
                                image = image,
                                context = context,
                                lifecycleScope = lifecycleScope
                            )
                            inspect = true
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.inspect),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else if (inspect) {
                    Button(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {
                            if (uri.value != null) {
                                Utils.shareImageByEmail(uri.value!!, context)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.share),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {
                            navController.navigate(MainDestinations.MAIN_SCREEN) {
                                popUpTo(MainDestinations.MAIN_SCREEN) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.next_image),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

}

private fun classifyImage(
    vm: MainViewModel,
    result: MutableState<Pair<String, Boolean>>,
    image: Bitmap?,
    context: Context,
    lifecycleScope: CoroutineScope,
) {
    lifecycleScope.launch {
        try {
            withContext(Dispatchers.IO) {
                val model: BrainTumorModel = BrainTumorModel.newInstance(context)
                val imageSize = 150
                // Creates inputs for reference.
                val inputFeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 150, 150, 3), DataType.FLOAT32)
                val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
                byteBuffer.order(ByteOrder.nativeOrder())
                val intValues = IntArray(imageSize * imageSize)
                image!!.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
                var pixel = 0
                //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
                for (i in 0 until imageSize) {
                    for (j in 0 until imageSize) {
                        val `val` = intValues[pixel++] // RGB
                        byteBuffer.putFloat((`val` shr 16 and 0xFF) * 1f)
                        byteBuffer.putFloat((`val` shr 8 and 0xFF) * 1f)
                        byteBuffer.putFloat((`val` and 0xFF) * 1f)
                    }
                }
                inputFeature0.loadBuffer(byteBuffer)

                // Runs model inference and gets result.
                val outputs: BrainTumorModel.Outputs = model.process(inputFeature0)
                val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
                // find the index of the class with the biggest confidence.
                val confidences = outputFeature0.floatArray
                // find the index of the class with the biggest confidence.

                val retIdx = confidences.indices.maxByOrNull { confidences[it] } ?: -1
                // Releases model resources if no longer used.
                model.close()
                val indexKey = Utils.classIndexKey[retIdx]
                result.value = "$indexKey / ${vm.getCurrentTime()}" to (retIdx == confidences.lastIndex || retIdx == confidences.lastIndex - 1)
            }
        } catch (e: IOException) {
            // handle exception
        }
    }
}