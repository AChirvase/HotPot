package com.alex.restaurantmodule.framework

import android.content.Context
import com.alex.hotpot.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/*
    Dependency Injection Module
 */

val coreModule = module {

    //ViewModels
    viewModel {
        com.alex.hotpot.presentation.LoginActivityViewModel(
            androidContext(),
            provideGoogleSignInClient(androidApplication()),
            provideFirebaseAuth()
        )
    }
}

private fun provideGoogleSignInClient(context: Context): GoogleSignInClient {
    FirebaseApp.initializeApp(context)

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Constants.GOOGLE_AUTH_CLIENT_ID)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, gso)
}

private fun provideFirebaseAuth() = FirebaseAuth.getInstance()
