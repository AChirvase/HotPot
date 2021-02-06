package com.alex.loginmodule.framework

import com.alex.loginmodule.presentation.LoginActivityViewModel
import com.alex.mainmodule.framework.provideFirebaseAuth
import com.alex.mainmodule.framework.provideGoogleSignInClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/*
    Dependency Injection Module
 */

val loginModule = module {

    //ViewModels
    viewModel {
        LoginActivityViewModel(
            androidContext(),
            provideGoogleSignInClient(androidApplication()),
            provideFirebaseAuth()
        )
    }
}
