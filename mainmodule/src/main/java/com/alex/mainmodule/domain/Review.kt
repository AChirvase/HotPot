package com.alex.mainmodule.domain

import com.alex.mainmodule.utils.Constants.DEFAULT_STRING
import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    var id: String = DEFAULT_STRING,
    var userEmail: String = DEFAULT_STRING,
    var reply: String = DEFAULT_STRING,
    var restaurantOverallEvaluation: Float = 0f,
    var title: String = DEFAULT_STRING,
    var description: String = DEFAULT_STRING,
    var dateInMillis: Long = 0,
    var visitDateInMillis: Long = 0
)