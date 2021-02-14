package com.alex.mainmodule.presentation.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.User
import kotlinx.android.synthetic.main.users_list_item.view.*

class UsersAdapter
    : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

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
        viewHolder.userRole.text = usersList[position].role

    }

    override fun getItemCount() = usersList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var userEmail: TextView = view.userEmailTv
        var userName: TextView = view.userNameTv
        var userRole: TextView = view.userRoleTv

        init {
            view.setOnClickListener {
                onItemClick?.invoke(usersList[adapterPosition])
            }
        }
    }
}