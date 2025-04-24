package org.ailingo.app.core.utils.coil

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.FileSystem

fun PlatformContext.asyncImageLoader() =
    ImageLoader
        .Builder(this)
        .crossfade(true)
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(this, 0.25)
                .strongReferencesEnabled(true)
                .build()
        }
        .logger(DebugLogger())
        .build()

/**
 * Enable disk cache for the [ImageLoader].
 */
fun ImageLoader.enableDiskCache() = this.newBuilder()
    .diskCache {
        DiskCache.Builder()
            .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
            .build()
    }.build()

//fun PlatformContext.asyncImageLoader() =
//    ImageLoader
//        .Builder(this)
//        .memoryCachePolicy(CachePolicy.ENABLED)
//        .memoryCache {
//            MemoryCache.Builder()
//                .maxSizePercent(this, 0.3)
//                .strongReferencesEnabled(true)
//                .build()
//        }
//        .crossfade(true)
//        .logger(DebugLogger())
//        .build()
//
//
//fun newDiskCache(): DiskCache {
//    return DiskCache
//        .Builder()
//        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
//        .maxSizeBytes(1024L * 1024 * 1024)
//        .build()
//}
//
//fun ImageLoader.enableDiskCache() = this.newBuilder()
//    .diskCachePolicy(CachePolicy.ENABLED).networkCachePolicy(CachePolicy.ENABLED).diskCache {
//        newDiskCache()
//    }.build()
