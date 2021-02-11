package com.alex.loginmodule.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alex.loginmodule.R
import com.alex.loginmodule.presentation.LoginActivityViewModel
import com.alex.loginmodule.presentation.LoginActivityViewState
import kotlinx.android.synthetic.main.sign_up_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class SignUpFragment : Fragment(), KoinComponent {
    private val viewModel: LoginActivityViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.sign_up_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonsOnClick()
        subscribeForViewStateChange()
    }

    private fun subscribeForViewStateChange() {
        viewModel.viewState.observe(viewLifecycleOwner, { viewState -> updateViewState(viewState) })
    }

    private fun setupButtonsOnClick() {
        googleSignInButton.setOnClickListener {
            viewModel.signInWithGoogle()
        }

        signUpBtn.setOnClickListener {
            viewModel.signUpWithEmailAndPassword(
                userNameEt.text.toString(),
                emailEt.text.toString(),
                passwordEt.text.toString()
            )
        }

        alreadyHaveAnAccountTv.setOnClickListener {
            viewModel.goToLogin()
        }
    }

    private fun updateViewState(viewState: LoginActivityViewState) {
        when (viewState) {
            LoginActivityViewState.SignUpWithUserAndPassword -> showProgressBar()
            LoginActivityViewState.LoginFailed -> hideProgressBar()
            LoginActivityViewState.LoginSuccess -> hideProgressBar()
            LoginActivityViewState.SignUpFailed -> showSignUpFailedError()
            LoginActivityViewState.ShowEmailError -> showInvalidEmailError()
            LoginActivityViewState.ShowPasswordError -> showInvalidPasswordError()
            LoginActivityViewState.ShowUserNameError -> showInvalidUserNameError()

            else -> {
            }
        }
    }

    private fun showInvalidEmailError() =
        Toast.makeText(context, R.string.email_error, Toast.LENGTH_SHORT).show()

    private fun showInvalidPasswordError() =
        Toast.makeText(context, R.string.password_error, Toast.LENGTH_SHORT).show()

    private fun showInvalidUserNameError() =
        Toast.makeText(context, R.string.user_name_error, Toast.LENGTH_SHORT).show()


    private fun showSignUpFailedError() {
        Toast.makeText(context, R.string.sign_up_error, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        loginProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        loginProgressBar.visibility = View.GONE
    }

}