package com.dreamfactory.brain_tumor_detection

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.dreamfactory.brain_tumor_detection.ui.MainNavGraph
import com.dreamfactory.brain_tumor_detection.ui.theme.BrainTumorDetectionTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val ARG_URI = "ARG_URI"
    }
//
//    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//        if (isGranted) {
//            takeImage()
//        }
//    }
//
//    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
//
//        if (isSuccess) {
//            latestTmpUri?.let { uri ->
//                val intent = Intent(baseContext, InspectActivity::class.java)
//                intent.putExtra(ARG_URI, uri)
//                startActivity(intent)
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrainTumorDetectionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavGraph(
                        finishActivity = { finish() }
                    )
                }
            }
        }
    }

//    private fun takeImage() {
//        lifecycleScope.launchWhenStarted {
//            getTmpFileUri().let { uri ->
//                latestTmpUri = uri
//                cameraLauncher.launch(uri)
//            }
//        }
//    }

//    private fun getTmpFileUri(): Uri {
//        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
//            createNewFile()
//            deleteOnExit()
//        }
//
//        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
//    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BrainTumorDetectionTheme {
        Greeting("Android")
    }
}