package com.alex.mainmodule.framework.local_datasource

import android.content.SharedPreferences
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.utils.Constants.USER_KEY
import com.google.gson.Gson


interface LocalDataSource {
    fun isUserAlreadyLoggedIn(): Boolean
    fun authenticateUser(user: User): Boolean
    fun logout(): Boolean
    fun getCurrentUser(): User
}

class LocalDataSourceImpl(private var sharedPreferences: SharedPreferences) : LocalDataSource {


    override fun isUserAlreadyLoggedIn() =
        !sharedPreferences.getString(USER_KEY, null).isNullOrEmpty()


    override fun authenticateUser(user: User) =
        sharedPreferences.edit().putString(USER_KEY, Gson().toJson(user)).commit()

    override fun logout() =
        sharedPreferences.edit().remove(USER_KEY).commit()

    override fun getCurrentUser(): User =
        Gson().fromJson(sharedPreferences.getString(USER_KEY, ""), User::class.java) ?: User()


}