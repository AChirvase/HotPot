package com.alex.loginmodule.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alex.loginmodule.utils.Constants.LOGIN_TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import org.koin.core.KoinComponent


sealed class LoginActivityViewState {
    object ShowLoginScreen : LoginActivityViewState()
    object SignInWithGoogle : LoginActivityViewState()
    object SignUpWithUserAndPassword : LoginActivityViewState()
    object ShowUserNameError : LoginActivityViewState()
    object ShowEmailError : LoginActivityViewState()
    object ShowPasswordError : LoginActivityViewState()
    object LoginFailed : LoginActivityViewState()
    object SignUpFailed : LoginActivityViewState()
    object SignUpSuccess : LoginActivityViewState()
    object LoginSuccess : LoginActivityViewState()
    object ShowSignUpScreen : LoginActivityViewState()
    object ExitApp : LoginActivityViewState()
}

class LoginActivityViewModel(
    private val context: Context,
    private var googleSignInClient: GoogleSignInClient,
    private var auth: FirebaseAuth
) : ViewModel(), KoinComponent {

    val viewState: MutableLiveData<LoginActivityViewState> by lazy {
        MutableLiveData<LoginActivityViewState>().also {
            it.value = LoginActivityViewState.ShowLoginScreen
        }
    }

    fun signInWithGoogle() {
        viewState.value = LoginActivityViewState.SignInWithGoogle
    }

    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    fun onGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthenticationWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            viewState.value = LoginActivityViewState.LoginFailed
            Log.w(LOGIN_TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthenticationWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    viewState.value = LoginActivityViewState.LoginSuccess
                } else {
                    viewState.value = LoginActivityViewState.LoginFailed
                    Log.w(LOGIN_TAG, "firebaseAuthenticationWithGoogle:failure", task.exception)
                }
            }
    }

    fun checkIfUserAlreadyAuthenticated() {
        //TODO
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        if (!isFormValid(email, password)) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    viewState.value = LoginActivityViewState.LoginSuccess
                } else {
                    Log.w(LOGIN_TAG, "signInWithEmailAndPassword:failure", task.exception)
                    viewState.value = LoginActivityViewState.LoginFailed
                }
            }
    }

    fun signUpWithEmailAndPassword(username: String, email: String, password: String) {
        if (!isFormValid(email, password, username)) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(username).build()
                )
                viewState.value = LoginActivityViewState.SignUpSuccess
            } else {
                Log.w(LOGIN_TAG, "createUserWithEmailAndPassword:failure", task.exception)
                viewState.value = LoginActivityViewState.SignUpFailed
            }
        }
    }

    private fun isFormValid(email: String, password: String, username: String? = null): Boolean {
        if (username?.isEmpty() == true) {
            viewState.value = LoginActivityViewState.ShowPasswordError
            return false
        }
        if (email.isEmpty()) {
            viewState.value = LoginActivityViewState.ShowEmailError
            return false
        }
        if (password.isEmpty()) {
            viewState.value = LoginActivityViewState.ShowPasswordError
            return false
        }
        return true
    }

    fun onBackPressed() {
        when (viewState.value) {
            is LoginActivityViewState.ShowSignUpScreen ->
                viewState.value = LoginActivityViewState.ShowLoginScreen
            else -> {
                viewState.value = LoginActivityViewState.ExitApp
            }
        }
    }

    fun goToSignUp() {
        viewState.value = LoginActivityViewState.ShowSignUpScreen
    }

    fun goToLogin() {
        viewState.value = LoginActivityViewState.ShowLoginScreen
    }


}