package com.alex.loginmodule.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alex.loginmodule.R
import kotlinx.android.synthetic.main.sign_up_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class SignUpFragment : Fragment(), KoinComponent {
    private val viewModel: com.alex.loginmodule.presentation.LoginActivityViewModel by sharedViewModel()

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
        viewModel.viewState.observe(this, { viewState -> updateViewState(viewState) })
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

    private fun updateViewState(viewState: com.alex.loginmodule.presentation.LoginActivityViewState) {
        when (viewState) {
            com.alex.loginmodule.presentation.LoginActivityViewState.SignUpWithUserAndPassword -> showProgressBar()
            com.alex.loginmodule.presentation.LoginActivityViewState.LoginFailed -> hideProgressBar()
            com.alex.loginmodule.presentation.LoginActivityViewState.LoginSuccess -> hideProgressBar()
            com.alex.loginmodule.presentation.LoginActivityViewState.SignUpFailed -> showSignUpFailedError()
            else -> {
            }
        }
    }

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