package com.alex.loginmodule

import android.app.Application
import com.alex.loginmodule.framework.loginModule
import com.alex.mainmodule.framework.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HotPotApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HotPotApplication)
            modules(listOf(loginModule, mainModule))
        }
    }
}