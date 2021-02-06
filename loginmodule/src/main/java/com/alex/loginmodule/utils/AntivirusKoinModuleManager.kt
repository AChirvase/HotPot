package com.alex.loginmodule.utils

import android.util.Log
import com.alex.loginmodule.framework.loginModule
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

object KoinModuleManager {
    private const val TAG = "AntivirusKoinModuleMng"

    fun loadKoinAntivirusModules() {
        try {
            loadKoinModules(loginModule)
        } catch (e: Exception) {
            Log.e(TAG, "TRIED TO LOAD ANTIVIRUS MODULE AGAIN")
        }
    }

    fun unloadKoinAntivirusModules() {
        try {
            unloadKoinModules(loginModule)
        } catch (e: Exception) {
            Log.e(TAG, "TRIED TO UNLOAD ANTIVIRUS MODULE AGAIN")
        }
    }
}