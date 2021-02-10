package com.alex.mainmodule.framework

import androidx.lifecycle.LiveData
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kiwimob.firestore.livedata.livedata


interface FirebaseDataSource {
    fun addRestaurant(restaurant: Restaurant): Task<DocumentReference>
    fun getRestaurantsListLiveData(): LiveData<List<Restaurant>>
    fun addReview(review: Review, restaurant: Restaurant)
    fun getUsersListLiveData(): LiveData<List<User>>
    fun addUser(user: User): Task<Void>
    fun replaceReview(restaurant: Restaurant, oldReview: Review, newReview: Review)
    fun deleteReview(restaurant: Restaurant, review: Review): Task<Void>
    fun deleteRestaurant(restaurant: Restaurant): Task<Void>
    fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant)
}

class FirebaseDataSourceImpl(
    firestore: FirebaseFirestore,
    private var auth: FirebaseAuth
) : FirebaseDataSource {

    private val restaurantsReference = firestore.collection("restaurants")
    private val usersReference = firestore.collection("users")
    private val reviewsFieldName = "reviews"

    override fun addRestaurant(restaurant: Restaurant) = restaurantsReference.add(restaurant)

    override fun getRestaurantsListLiveData() =
        restaurantsReference.livedata(Restaurant::class.java)

    override fun addReview(review: Review, restaurant: Restaurant) {
        review.dateInMillis = System.currentTimeMillis()
        review.userEmail = auth.currentUser?.email.toString()

        restaurantsReference.document(restaurant.id)
            .update(reviewsFieldName, FieldValue.arrayUnion(review))
    }

    override fun getUsersListLiveData() = usersReference.livedata(User::class.java)

    override fun addUser(user: User) = usersReference.document(user.email).set(user)

    override fun replaceReview(restaurant: Restaurant, oldReview: Review, newReview: Review) {
        newReview.dateInMillis = oldReview.dateInMillis
        restaurantsReference.document(restaurant.id)
            .update(reviewsFieldName, FieldValue.arrayRemove(oldReview))
        restaurantsReference.document(restaurant.id)
            .update(reviewsFieldName, FieldValue.arrayUnion(newReview))
    }

    override fun deleteReview(restaurant: Restaurant, review: Review) =
        restaurantsReference.document(restaurant.id)
            .update(reviewsFieldName, FieldValue.arrayRemove(review))

    override fun deleteRestaurant(restaurant: Restaurant) =
        restaurantsReference.document(restaurant.id).delete()


    override fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant) {
        newRestaurant.reviews = oldRestaurant.reviews
        restaurantsReference.document(oldRestaurant.id).set(newRestaurant)
    }


}