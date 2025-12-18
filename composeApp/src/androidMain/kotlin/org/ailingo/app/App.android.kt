package org.ailingo.app

import android.Manifest
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import org.ailingo.app.di.initKoin
import org.ailingo.app.features.profileupdate.presentation.ImagePickerActivityResult
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@AndroidApp)
        }
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ImagePickerActivityResult.init(activityResultRegistry, this)
        actionBar?.hide()
        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        Log.d("Permission", "RECORD_AUDIO permission granted")
                    } else {
                        Log.d("Permission", "RECORD_AUDIO permission denied")
                    }
                }
            )
            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImagePickerActivityResult.release()
    }
}