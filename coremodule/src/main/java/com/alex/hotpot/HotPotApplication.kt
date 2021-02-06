package com.alex.hotpot

import android.app.Application
import com.alex.restaurantmodule.framework.coreModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HotPotApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HotPotApplication)
            modules(coreModule)
        }
    }
}