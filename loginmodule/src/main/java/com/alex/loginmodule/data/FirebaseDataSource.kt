package com.alex.loginmodule.data

import android.content.Intent
import com.alex.mainmodule.domain.User

interface LoginFirebaseDataSource {
    fun getGoogleSignInIntent(): Intent
    suspend fun signInWithEmailAndPassword(user: User): Boolean
    suspend fun signUpWithEmailAndPassword(user: User): Boolean
    fun logout(): Boolean
}