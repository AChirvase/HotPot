package com.alex.loginmodule.framework

import android.content.Intent
import com.alex.loginmodule.data.LoginFirebaseDataSource
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.framework.MainFirebaseDataSource
import com.alex.mainmodule.utils.AESCrypt.decrypt
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginFirebaseDataSourceImpl(
    private var mainFirebaseDataSource: MainFirebaseDataSource,
    private var googleSignInClient: GoogleSignInClient
) : LoginFirebaseDataSource {

    override fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    override suspend fun signInWithEmailAndPassword(user: User): Boolean =
        suspendCoroutine { continuation ->
            if (isUserAlreadyLoggedIn()) {
                continuation.resume(true)
            }

            mainFirebaseDataSource.getUserFromDataSource(user.email)
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userFromDataSource = document.toObject(User::class.java)
                        if (isPasswordCorrect(userFromDataSource?.password, user.password)) {
                            userFromDataSource?.let { mainFirebaseDataSource.authenticateUser(it) }
                            continuation.resume(true)
                        } else {
                            continuation.resume(false)
                        }
                    } else {
                        continuation.resume(false)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }


        }

    private fun isPasswordCorrect(dataSourcePassword: String?, password: String): Boolean {
        return if (dataSourcePassword == null) {
            false
        } else {
            password == decrypt(dataSourcePassword)
        }
    }

    private fun isUserAlreadyLoggedIn() =
        mainFirebaseDataSource.isUserAlreadyLoggedIn()

    override suspend fun signUpWithEmailAndPassword(user: User): Boolean =
        suspendCoroutine { continuation ->
            mainFirebaseDataSource.addUser(user)
                .addOnSuccessListener {
                    mainFirebaseDataSource.authenticateUser(user)
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
        }

    override fun logout() = mainFirebaseDataSource.logout()

}