package com.alex.mainmodule.domain

import com.alex.mainmodule.utils.Constants.DEFAULT_STRING

enum class Role {
    REGULAR, OWNER, ADMIN
}

data class User(
    var name: String = DEFAULT_STRING,
    var email: String = DEFAULT_STRING,
    var password: String = DEFAULT_STRING,
    var role: String = Role.REGULAR.name,
    var image: String = DEFAULT_STRING
)