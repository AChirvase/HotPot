package com.alex.loginmodule.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alex.loginmodule.R
import com.alex.loginmodule.presentation.fragments.LoginFragment
import com.alex.loginmodule.presentation.fragments.SignUpFragment
import com.alex.mainmodule.presentation.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginActivityViewModel by viewModel()

    private val loginFragment = LoginFragment()
    private val signUpFragment = SignUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        subscribeForViewStateChange()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkIfUserAlreadyAuthenticated()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private fun subscribeForViewStateChange() {
        viewModel.viewState.observe(this, { viewState -> updateViewState(viewState) })
    }

    private fun updateViewState(viewState: LoginActivityViewState) {
        supportFragmentManager.executePendingTransactions()
        when (viewState) {
            LoginActivityViewState.ShowLoginScreen -> showLoginFragment()
            LoginActivityViewState.SignInWithGoogle -> startIntentSignInGoogle()
            LoginActivityViewState.ShowSignUpScreen -> showSignUpFragment()
            LoginActivityViewState.LoginFailed -> showLoginFailedError()
            LoginActivityViewState.LoginSuccess -> startRestaurantsActivity()
            LoginActivityViewState.SignUpSuccess -> startRestaurantsActivity()
            LoginActivityViewState.ExitApp -> finishAffinity()
            else -> {
            }
        }
    }


    private fun showLoginFailedError() =
        Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show()

    private fun startRestaurantsActivity() {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLoginFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up
            )
            .replace(
                R.id.loginActivityFragment,
                loginFragment
            ).addToBackStack(null)
            .commit()
    }

    private fun showSignUpFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up
            )
            .replace(
                R.id.loginActivityFragment,
                signUpFragment
            ).addToBackStack(null)
            .commit()
    }

    private fun startIntentSignInGoogle() = resultLauncher.launch(viewModel.getGoogleSignInIntent())

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onGoogleSignInResult(result.data)
            }
        }

}