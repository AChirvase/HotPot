package com.alex.mainmodule.domain

import com.alex.mainmodule.utils.Constants.DEFAULT_STRING

data class User(
    var name: String = DEFAULT_STRING,
    var email: String = DEFAULT_STRING,
    var phoneNumber: String = DEFAULT_STRING
)