package com.dreamfactory.brain_tumor_detection.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dreamfactory.brain_tumor_detection.R

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
    Box(modifier = Modifier.padding(innerPadding)) {
        vm.imageUri?.let {
            AsyncImage(model = vm.imageUri, contentDescription = "")
        }
    }

}