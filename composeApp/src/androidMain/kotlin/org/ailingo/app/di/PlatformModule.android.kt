package org.ailingo.app.di

import android.content.Context
import org.ailingo.app.AndroidApp
import org.ailingo.app.features.dictionary.historysearch.di.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DriverFactory(get()) }
}

fun provideAppContext(): Context {
    return AndroidApp.INSTANCE
}