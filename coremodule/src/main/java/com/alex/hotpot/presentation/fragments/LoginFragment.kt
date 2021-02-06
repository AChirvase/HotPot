package com.alex.hotpot.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alex.hotpot.R
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class LoginFragment : Fragment(), KoinComponent {
    private val viewModel: com.alex.hotpot.presentation.LoginActivityViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonsOnClick()
        subscribeForViewStateChange()
    }

    private fun setupButtonsOnClick() {
        googleSignInButton.setOnClickListener {
            viewModel.signInWithGoogle()
        }

        loginBtn.setOnClickListener {
            viewModel.signInWithEmailAndPassword(
                emailEt.text.toString(),
                passwordEt.text.toString()
            )
        }

        signUpTv.setOnClickListener {
            viewModel.goToSignUp()
        }
    }


    private fun subscribeForViewStateChange() {
        viewModel.viewState.observe(this, { viewState -> updateViewState(viewState) })
    }

    private fun updateViewState(viewState: com.alex.hotpot.presentation.LoginActivityViewState) {
        when (viewState) {
            com.alex.hotpot.presentation.LoginActivityViewState.SignInWithGoogle -> showProgressBar()
            com.alex.hotpot.presentation.LoginActivityViewState.LoginFailed -> hideProgressBar()
            com.alex.hotpot.presentation.LoginActivityViewState.LoginSuccess -> hideProgressBar()
            com.alex.hotpot.presentation.LoginActivityViewState.ShowEmailError -> showInvalidEmailError()
            com.alex.hotpot.presentation.LoginActivityViewState.ShowPasswordError -> showInvalidPasswordError()
            else -> {
            }
        }
    }

    private fun showInvalidEmailError() =
        Toast.makeText(context, R.string.email_error, Toast.LENGTH_SHORT).show()

    private fun showInvalidPasswordError() =
        Toast.makeText(context, R.string.password_error, Toast.LENGTH_SHORT).show()

    private fun showProgressBar() {
        loginProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        loginProgressBar.visibility = View.GONE
    }

}