package com.alex.mainmodule.data

import com.alex.mainmodule.domain.User

interface LocalDataSource {
    fun isUserAlreadyLoggedIn(): Boolean
    fun authenticateUser(user: User): Boolean
    fun logout(): Boolean
    fun getCurrentUser(): User
}
