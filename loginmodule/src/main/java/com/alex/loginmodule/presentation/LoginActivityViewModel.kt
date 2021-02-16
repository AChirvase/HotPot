package com.alex.loginmodule.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alex.loginmodule.data.LoginRepositoryImpl
import com.alex.loginmodule.utils.Constants
import com.alex.loginmodule.utils.Constants.LOGIN_TAG
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.presentation.LoginNavigator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.qualifier.named


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
    private val repository: LoginRepositoryImpl
) : ViewModel(), LoginNavigator, KoinComponent {

    val viewState: MutableLiveData<LoginActivityViewState> by lazy {
        MutableLiveData<LoginActivityViewState>().also {
            it.value = LoginActivityViewState.ShowLoginScreen
        }
    }

    fun signInWithGoogle() {
        Toast.makeText(context, "TO DO", Toast.LENGTH_SHORT).show()
    }

    fun getGoogleSignInIntent(): Intent = repository.getGoogleSignInIntent()

    fun onGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            signInWithEmailAndPassword(account.email!!, "12345678")
            viewState.postValue(LoginActivityViewState.LoginSuccess)
        } catch (e: ApiException) {
            viewState.value = LoginActivityViewState.LoginFailed
            Log.w(LOGIN_TAG, "Google sign in failed", e)
        }
    }

    fun checkIfUserAlreadyAuthenticated() {
        if (repository.isUserAlreadyAuthenticated()) {
            viewState.value = LoginActivityViewState.LoginSuccess
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        if (!isFormValid(email, password)) {
            return
        }

        val user = User(email = email, password = password)
        GlobalScope.launch {
            if (repository.signInWithEmailAndPassword(user)) {
                viewState.postValue(LoginActivityViewState.LoginSuccess)
            } else {
                viewState.postValue(LoginActivityViewState.LoginFailed)
            }
        }
    }

    fun signUpWithEmailAndPassword(username: String, email: String, password: String) {
        if (!isFormValid(email, password, username)) {
            return
        }
        val role = Role.REGULAR
        val user = User(username, email, password, role.name)

        GlobalScope.launch {
            if (repository.signUpWithEmailAndPassword(user)) {
                viewState.postValue(LoginActivityViewState.SignUpSuccess)
            } else {
                viewState.postValue(LoginActivityViewState.SignUpFailed)
            }
        }
    }

    private fun isFormValid(email: String, password: String, username: String? = null): Boolean {
        if (username?.isEmpty() == true) {
            viewState.value = LoginActivityViewState.ShowUserNameError
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

    override fun logout() {
        repository.signOut()
        goToLogin()
        val intent = Intent(context, get(named(Constants.LOGIN_ACTIVITY_CLASS)))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}