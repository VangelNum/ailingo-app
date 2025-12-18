package org.ailingo.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.setSingletonImageLoaderFactory
import org.ailingo.app.core.utils.coil.asyncImageLoader
import org.ailingo.app.core.utils.coil.enableDiskCache
import org.ailingo.app.core.utils.deviceinfo.util.PlatformName

@Composable
internal fun App(
    navController: NavHostController = rememberNavController()
) {
    setSingletonImageLoaderFactory { context ->
        if (getPlatformName() == PlatformName.Web) {
            context.asyncImageLoader()
        } else {
            context.asyncImageLoader().enableDiskCache()
        }
    }
    AiLingoNavGraph(
        navController = navController
    )
}

internal expect fun openUrl(url: String?)
internal expect fun getPlatformName(): PlatformName
internal expect fun playSound(sound: String)