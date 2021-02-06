package com.alex.mainmodule.framework

import androidx.lifecycle.LiveData
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kiwimob.firestore.livedata.livedata

interface FirebaseDataSource {
    fun addRestaurant(restaurant: Restaurant): Task<DocumentReference>
    fun getRestaurantsLiveData(): LiveData<List<Restaurant>>
    fun addReview(review: Review, restaurant: Restaurant): Task<DocumentReference>

}

class FirebaseDataSourceImpl(
    private val googleSignInClient: GoogleSignInClient,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseDataSource {

    private val restaurantsReference = firestore.collection("restaurants")

    override fun addRestaurant(restaurant: Restaurant) = restaurantsReference.add(restaurant)

    override fun getRestaurantsLiveData() = restaurantsReference.livedata(Restaurant::class.java)


    override fun addReview(review: Review, restaurant: Restaurant) =
        restaurantsReference.document(restaurant.id).collection("reviews").add(review)


}