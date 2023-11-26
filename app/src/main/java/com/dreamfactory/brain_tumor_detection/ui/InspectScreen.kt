package com.dreamfactory.brain_tumor_detection.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.dreamfactory.brain_tumor_detection.R
import com.dreamfactory.brain_tumor_detection.Utils
import com.dreamfactory.brain_tumor_detection.ml.BrainTumorModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
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

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .allowHardware(false)
            .build()
    }

    Box(modifier = Modifier.padding(innerPadding)) {
        vm.imageUri?.let {
            AsyncImage(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                imageLoader = imageLoader,
                model = vm.imageUri,
                contentDescription = "",
                onSuccess = { imageState ->
                    val image = Bitmap.createScaledBitmap(
                        imageState.result.drawable.toBitmap(),
                        150,
                        150,
                        false
                    )
                    classifyImage(image, context)
                },
            )
        }
    }

}

private fun classifyImage(image: Bitmap?, context: Context) {
    try {
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

        Utils.classIndexKey[retIdx]

        // Releases model resources if no longer used.
        model.close()
    } catch (e: IOException) {
        // handle exception
    }
}