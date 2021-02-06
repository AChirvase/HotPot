package com.alex.loginmodule.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alex.loginmodule.R
import com.alex.loginmodule.presentation.fragments.LoginFragment
import com.alex.loginmodule.presentation.fragments.SignUpFragment
import com.alex.loginmodule.utils.Constants.GOOGLE_SIGN_IN
import com.alex.loginmodule.utils.KoinModuleManager.loadKoinAntivirusModules
import com.alex.loginmodule.utils.KoinModuleManager.unloadKoinAntivirusModules
import com.alex.mainmodule.presentation.RestaurantsActivity
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginActivityViewModel by viewModel()

    private val loginFragment = LoginFragment()
    private val signUpFragment = SignUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinAntivirusModules()
        setContentView(R.layout.activity_login)
        subscribeForViewStateChange()
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinAntivirusModules()
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
            LoginActivityViewState.ExitApp -> finishAndRemoveTask()
            else -> {
            }
        }
    }


    private fun showLoginFailedError() =
        Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show()

    private fun startRestaurantsActivity() {
        intent = Intent(this, RestaurantsActivity::class.java)
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

    private fun startIntentSignInGoogle() =
        startActivityForResult(viewModel.getGoogleSignInIntent(), GOOGLE_SIGN_IN)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            viewModel.onGoogleSignInResult(data)
        }
    }

}