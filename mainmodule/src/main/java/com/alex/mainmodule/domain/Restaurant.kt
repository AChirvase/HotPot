package com.alex.mainmodule.domain

import com.alex.mainmodule.utils.Constants.DEFAULT_STRING
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Restaurant(
    @DocumentId
    val id: String = DEFAULT_STRING,
    var name: String = DEFAULT_STRING,
    var ownerEmail: String = DEFAULT_STRING,
    var picture: String = DEFAULT_STRING,
    var menu: ArrayList<String> = arrayListOf(DEFAULT_STRING, DEFAULT_STRING),
    var keyWords: ArrayList<String> = arrayListOf(DEFAULT_STRING, DEFAULT_STRING),
    var perks: ArrayList<String> = arrayListOf(DEFAULT_STRING, DEFAULT_STRING),
    var address: String = DEFAULT_STRING,
    var phoneNumber: String = DEFAULT_STRING,
    var reviews: ArrayList<Review> = arrayListOf(),
    @Exclude
    var rating: Float = 0f

)