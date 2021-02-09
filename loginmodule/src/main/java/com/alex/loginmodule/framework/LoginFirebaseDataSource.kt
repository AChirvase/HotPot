package com.alex.loginmodule.framework

import android.content.Intent
import android.util.Log
import com.alex.loginmodule.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface LoginFirebaseDataSource {

    fun getGoogleSignInIntent(): Intent
    fun isUserAlreadyAuthenticated(): Boolean
    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean
    suspend fun firebaseAuthenticationWithGoogle(idToken: String): Boolean
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Boolean
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
}

class LoginFirebaseDataSourceImpl(
    private var googleSignInClient: GoogleSignInClient,
    private var auth: FirebaseAuth
) : LoginFirebaseDataSource {

    override fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    override fun isUserAlreadyAuthenticated(): Boolean = auth.currentUser != null

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean =
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        Log.w(
                            Constants.LOGIN_TAG,
                            "signInWithEmailAndPassword:failure",
                            task.exception
                        )
                        continuation.resume(false)
                    }
                }
        }

    override suspend fun firebaseAuthenticationWithGoogle(idToken: String): Boolean =
        suspendCoroutine { continuation ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        Log.w(
                            Constants.LOGIN_TAG,
                            "firebaseAuthenticationWithGoogle:failure",
                            task.exception
                        )
                        continuation.resume(false)
                    }
                }
        }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Boolean =
        suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        Log.w(
                            Constants.LOGIN_TAG,
                            "createUserWithEmailAndPassword:failure",
                            task.exception
                        )
                        continuation.resume(false)
                    }
                }

        }

    override fun signOut() = auth.signOut()
    override fun getCurrentUser() = auth.currentUser

}