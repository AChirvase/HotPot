package com.alex.mainmodule.framework

import android.content.Context
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.utils.Constants.GOOGLE_AUTH_CLIENT_ID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


/*
    Dependency Injection Module
 */
val mainModule = module {

    //Data sources
    single<FirebaseDataSource> {
        FirebaseDataSourceImpl(
            provideFirebaseFirestore()
        )
    }

    //Repository
    single { Repository(get()) }

    //ViewModels
    viewModel { MainActivityViewModel(androidContext(), get()) }

}


fun provideGoogleSignInClient(context: Context): GoogleSignInClient {
    FirebaseApp.initializeApp(context)

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(GOOGLE_AUTH_CLIENT_ID)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, gso)
}

fun provideFirebaseAuth() = FirebaseAuth.getInstance()
fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()