package com.alex.mainmodule.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.fragments.adapters.UsersAdapter
import kotlinx.android.synthetic.main.users_list_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class UsersListFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.users_list_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        val usersAdapter = UsersAdapter()

        viewModel.filteredUsersListLiveData.observe(viewLifecycleOwner,
            {
                usersAdapter.usersList = it as ArrayList<User>
                usersAdapter.notifyDataSetChanged()
            })

        usersAdapter.onItemClick = { user ->
            viewModel.showEditUserScreen(user)
        }

        specificsRecyclerView.layoutManager = LinearLayoutManager(context)
        specificsRecyclerView.adapter = usersAdapter
    }

}

