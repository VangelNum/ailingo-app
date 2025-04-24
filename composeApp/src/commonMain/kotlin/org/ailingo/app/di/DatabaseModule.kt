package org.ailingo.app.di

import org.koin.dsl.module

val sqlDelightModule = module {
    single { SharedDatabase(get()) }
}