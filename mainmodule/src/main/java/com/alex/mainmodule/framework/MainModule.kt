package com.alex.mainmodule.framework

import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.presentation.MainActivityViewModel
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
            provideFirebaseFirestore(),
            provideFirebaseAuth()
        )
    }

    //Repository
    single { Repository(get()) }

    //ViewModels
    viewModel {
        MainActivityViewModel(
            androidContext(),
            get(),
            get()
        )
    }

}

fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()
fun provideFirebaseAuth() = FirebaseAuth.getInstance()
