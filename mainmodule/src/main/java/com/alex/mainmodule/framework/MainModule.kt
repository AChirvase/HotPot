package com.alex.mainmodule.framework

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.framework.local_datasource.LocalDataSource
import com.alex.mainmodule.framework.local_datasource.LocalDataSourceImpl
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.utils.Constants.MAIN_SHARED_PREFERENCES
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
    single<MainFirebaseDataSource> {
        MainFirebaseDataSourceImpl(
            get(),
            provideFirebaseFirestore()
        )
    }

    single<LocalDataSource> { LocalDataSourceImpl(provideSharedPreferences(androidContext())) }

    //Repository
    single { Repository(get(), get()) }

    //ViewModels
    viewModel {
        MainActivityViewModel(
            androidContext(),
            get(),
            get()
        )
    }

}

fun provideSharedPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(MAIN_SHARED_PREFERENCES, MODE_PRIVATE)

fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()
fun provideFirebaseAuth() = FirebaseAuth.getInstance()
