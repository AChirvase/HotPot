package com.alex.mainmodule.presentation.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import kotlinx.android.synthetic.main.add_user_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class AddUserFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()
    private var userRole: String = Role.REGULAR.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addOnAddUserListener()

        return inflater.inflate(
            R.layout.add_user_fragment,
            container,
            false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.viewState.value == MainActivityViewState.ShowEditUserScreen) {
            showEditMode()
        }

    }

    private fun showEditMode() {
        userEmailTvEditModeTv.visibility = View.VISIBLE
        deleteUserTv.visibility = View.VISIBLE

        viewModel.selectedUserLiveData.observe(viewLifecycleOwner) {
            userNameTv.setText(it.name)
            userEmailTv.setText(it.email)
            updateSelectedUserRole(it.role)
        }

        deleteUserTv.setOnClickListener {
            viewModel.deleteUser()
        }

        regularUserTv.setOnClickListener {
            updateSelectedUserRole(Role.REGULAR.name)
        }

        ownerUserTv.setOnClickListener {
            updateSelectedUserRole(Role.OWNER.name)
        }

        adminUserTv.setOnClickListener {
            updateSelectedUserRole(Role.ADMIN.name)
        }
    }

    private fun updateSelectedUserRole(role: String) {
        userRole = role
        regularUserTv.background = getDrawableForSelectedRole(role == Role.REGULAR.name)
        ownerUserTv.background = getDrawableForSelectedRole(role == Role.OWNER.name)
        adminUserTv.background = getDrawableForSelectedRole(role == Role.ADMIN.name)
    }

    private fun getDrawableForSelectedRole(isSelected: Boolean): Drawable? {
        return if (isSelected) {
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.rectangle_round_corners_primary_color
            )
        } else {
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.rectangle_round_corners_gray_color
            )
        }
    }

    private fun addOnAddUserListener() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                MainActivityViewState.AddUser -> viewModel.addUser(getUser())
                MainActivityViewState.EditUser -> viewModel.editUser(getUser())
                else -> {
                }
            }
        })
    }

    private fun getUser() = User(
        name = userNameTv.text.toString(),
        email = userEmailTv.text.toString(),
        password = userPasswordTv.text.toString(),
        role = userRole
    )
}