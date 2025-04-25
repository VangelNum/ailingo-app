package org.ailingo.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImagePickerActivityResult.release()
    }
}