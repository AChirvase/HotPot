package com.alex.restaurantsmodule.domain

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    var id: String,
    var restaurantId: String,
    var userId: String,
    var replyId: String,
    var restaurantOverallEvaluation: Int,
    var priceEvaluation: Int,
    var dish: String,
    var title: String,
    var description: String,
    var dateInMillis: Long
)