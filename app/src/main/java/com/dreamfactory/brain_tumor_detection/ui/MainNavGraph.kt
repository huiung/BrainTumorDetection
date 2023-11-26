package com.dreamfactory.brain_tumor_detection.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

object MainDestinations {
    const val MAIN_SCREEN = "main_screen"
    const val INFO_SCREEN = "info_screen"
    const val INSPECT_SCREEN = "inspect_screen"
}
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
    finishActivity: () -> Unit = { }
) {
    NavHost(
        navController = navController,
        startDestination = MainDestinations.MAIN_SCREEN
    ) {
        composable(
            route = MainDestinations.MAIN_SCREEN
        ) { backStackEntry ->
            // Creates a ViewModel from the current BackStackEntry
            // Available in the androidx.hilt:hilt-navigation-compose artifact
            val vm = hiltViewModel<MainViewModel>()
            MainRoute(vm, navController) {
                if (!navController.popBackStack()) {
                    finishActivity()
                }
            }
        }

        composable(
            route = MainDestinations.INFO_SCREEN
        ) { backStackEntry ->
            // Creates a ViewModel from the current BackStackEntry
            // Available in the androidx.hilt:hilt-navigation-compose artifact
            InfoRoute(navController) {
                if (!navController.popBackStack()) {
                    finishActivity()
                }
            }
        }

        composable(
            route = MainDestinations.INSPECT_SCREEN
        ) { backStackEntry ->
            // Creates a ViewModel from the current BackStackEntry
            // Available in the androidx.hilt:hilt-navigation-compose artifact
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainDestinations.MAIN_SCREEN)
            }
            val vm = hiltViewModel<MainViewModel>(parentEntry)
            InspectRoute(vm, navController) {
                if (!navController.popBackStack()) {
                    finishActivity()
                }
            }
        }
    }
}