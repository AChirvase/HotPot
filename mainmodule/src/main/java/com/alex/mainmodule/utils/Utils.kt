package com.alex.mainmodule.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alex.mainmodule.R

object Utils {

    fun hideSystemUI(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    fun showFragment(
        fragment: Fragment,
        fragmentManager: FragmentManager,
        replace: Boolean = false
    ) {
        if (fragment.isAdded) {
            fragmentManager.popBackStackImmediate(fragment.javaClass.name, 0)
            return
        }
        val transaction = fragmentManager.beginTransaction()

        if (replace) {
            transaction.replace(R.id.mainActivityFragmentContainer, fragment)
        } else {
            transaction.add(R.id.mainActivityFragmentContainer, fragment)
        }

        transaction.addToBackStack(fragment.javaClass.name).commit()
    }
}