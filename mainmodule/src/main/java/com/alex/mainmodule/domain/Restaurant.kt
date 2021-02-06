package com.alex.mainmodule.domain

import com.google.firebase.firestore.DocumentId

data class Restaurant(
    @DocumentId
    val id: String,
    var name: String,
    var picture: String,
    var menu: ArrayList<String>,
    var keyWords: ArrayList<String>,
    var perks: ArrayList<String>,
    var address: String,
    var phoneNumber: String,
    var reviews: ArrayList<Review>
)