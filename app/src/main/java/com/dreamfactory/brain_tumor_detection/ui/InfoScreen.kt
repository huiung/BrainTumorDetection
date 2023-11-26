package com.dreamfactory.brain_tumor_detection.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
fun InfoRoute(
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
                    Text(stringResource(id = R.string.info), modifier = Modifier.padding(start = 10.dp))
                },
                actions = {
                    IconButton(
                        modifier = Modifier.padding(end = 10.dp),
                        onClick = onBackPressed
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        InfoScreen(innerPadding)
    }
}

@Composable
fun InfoScreen(
    innerPadding: PaddingValues
) {

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 22.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(id = R.string.info_title_text_1),
            style = (MaterialTheme.typography.bodyMedium),
        )
        Text(
            text = stringResource(id = R.string.info_description_text_1),
            style = (MaterialTheme.typography.bodyMedium),
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(58.dp))
        Text(
            text = stringResource(id = R.string.info_title_text_2),
            style = (MaterialTheme.typography.bodyMedium),
        )
        Text(
            text = stringResource(id = R.string.info_description_text_2),
            style = (MaterialTheme.typography.bodyMedium),
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview
@Composable
fun InfoScreenPreview() {
    InfoRoute(navController = rememberNavController(), onBackPressed = { })
}