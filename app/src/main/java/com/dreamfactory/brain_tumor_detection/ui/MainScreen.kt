package com.dreamfactory.brain_tumor_detection.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.dreamfactory.brain_tumor_detection.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
fun MainRoute(
    vm: MainViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alarm),
                            contentDescription = ""
                        )
                    }
                    IconButton(
                        modifier = Modifier.padding(end = 10.dp),
                        onClick = { }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        MainScreen(innerPadding, vm, navController, onBackPressed)
    }
}

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    vm: MainViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleScope = rememberCoroutineScope()

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                navController.navigate(MainDestinations.INSPECT_SCREEN)
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            lifecycleScope.launch {
                takeImage(context, vm, cameraLauncher)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->

        uri ?: return@rememberLauncherForActivityResult
        vm.imageUri = uri
        navController.navigate(MainDestinations.INSPECT_SCREEN)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(start = 22.dp, end = 10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(MainDestinations.INFO_SCREEN)
                }
                .padding(top = 12.dp, bottom = 12.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.info),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_right),
                contentDescription = ""
            )
        }
        Text(
            text = stringResource(id = R.string.brain_tumor_detection),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.25f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    if (checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        lifecycleScope.launch {
                            takeImage(context, vm, cameraLauncher)
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.camera),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Button(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    galleryLauncher.launch("image/*")
                }
            ) {
                Text(
                    text = stringResource(id = R.string.gallery),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(45.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainRoute(
        vm = MainViewModel(),
        navController = NavController(LocalContext.current),
        onBackPressed = {}
    )
}

private suspend fun takeImage(
    context: Context,
    vm: MainViewModel,
    cameraLauncher: ActivityResultLauncher<Uri>
) {
    vm.imageUri = getTempFileUri(context)
    vm.imageUri?.let { uri ->
        cameraLauncher.launch(uri)
    }
}

private suspend fun getTempFileUri(context: Context): Uri {
    val tmpFile = withContext(Dispatchers.IO) {
        File.createTempFile("tmp_image_file", ".png", context.cacheDir)
    }.apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(
        context.applicationContext,
        "${context.packageName}.provider",
        tmpFile
    )
}