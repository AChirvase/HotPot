package com.alex.loginmodule.framework

import android.content.Context
import com.alex.loginmodule.data.LoginRepository
import com.alex.loginmodule.presentation.LoginActivity
import com.alex.loginmodule.presentation.LoginActivityViewModel
import com.alex.loginmodule.utils.Constants.LOGIN_ACTIVITY_CLASS
import com.alex.mainmodule.presentation.LoginNavigator
import com.alex.mainmodule.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/*
    Dependency Injection Module
 */

val loginModule = module {

    //Data sources
    single<LoginFirebaseDataSource> {
        LoginFirebaseDataSourceImpl(
            get(),
            provideGoogleSignInClient(androidApplication())
        )
    }

    //Repository
    single { LoginRepository(get(), get()) }

    //ViewModels
    viewModel { LoginActivityViewModel(androidContext(), get()) }

    //Navigator
    single<LoginNavigator> { LoginActivityViewModel(androidContext(), get()) }

    single(named(LOGIN_ACTIVITY_CLASS)) { LoginActivity::class.java }

}

fun provideGoogleSignInClient(context: Context): GoogleSignInClient {
    FirebaseApp.initializeApp(context)

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Constants.GOOGLE_AUTH_CLIENT_ID)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, gso)
}

