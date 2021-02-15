package com.alex.mainmodule.framework

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.data.RepositoryImpl
import com.alex.mainmodule.framework.local_datasource.LocalDataSource
import com.alex.mainmodule.framework.local_datasource.LocalDataSourceImpl
import com.alex.mainmodule.framework.remote_datasource.HotPotService
import com.alex.mainmodule.framework.remote_datasource.NetworkResponseHandler
import com.alex.mainmodule.framework.remote_datasource.RemoteDataSource
import com.alex.mainmodule.framework.remote_datasource.RemoteDataSourceImpl
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.utils.Constants.MAIN_SHARED_PREFERENCES
import com.alex.mainmodule.utils.Constants.REST_API_BASE_URL
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*
    Dependency Injection Module
 */
val mainModule = module {

    //REST API mechanisms
    single { NetworkResponseHandler() }
    single { provideLoggingInterceptor() }
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideHotpotService(get()) }

    //Data Sources
    single<LocalDataSource> { LocalDataSourceImpl(provideSharedPreferences(androidContext())) }
    single<RemoteDataSource> { RemoteDataSourceImpl(get()) }
    single<MainFirebaseDataSource> {
        MainFirebaseDataSourceImpl(
            get(),
            provideFirebaseFirestore()
        )
    }


    //Repository
    single<Repository> { RepositoryImpl(get(), get(), get()) }

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

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(REST_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.BODY
    return logger
}

private fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
    OkHttpClient()
        .newBuilder()
        //logging http calls and errors
        .addInterceptor(loggingInterceptor)
        .build()

private fun provideHotpotService(retrofit: Retrofit): HotPotService =
    retrofit.create(HotPotService::class.java)

