package com.alex.mainmodule.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.presentation.MainActivityViewModel
import kotlinx.android.synthetic.main.users_list_fragment.*
import kotlinx.android.synthetic.main.users_list_item.view.*
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
        val usersAdapter = UserRecyclerAdapter()

        viewModel.usersListLiveData.observe(viewLifecycleOwner,
            {
                usersAdapter.usersList = it as ArrayList<User>
                usersAdapter.notifyDataSetChanged()
            })

        usersAdapter.onItemClick = { user ->
            viewModel.onUserClicked(user)
        }

        specificsRecyclerView.layoutManager = LinearLayoutManager(context)
        specificsRecyclerView.adapter = usersAdapter
    }

    class UserRecyclerAdapter
        : RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {

        var usersList = ArrayList<User>()

        //this is a callback
        var onItemClick: ((User) -> Unit)? = null

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.users_list_item, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.userEmail.text = usersList[position].email
            viewHolder.userName.text = usersList[position].name
        }

        override fun getItemCount() = usersList.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var userEmail: TextView = view.userEmailTv
            var userName: TextView = view.userNameTv

            init {
                view.setOnClickListener {
                    onItemClick?.invoke(usersList[adapterPosition])
                }
            }
        }
    }

}

