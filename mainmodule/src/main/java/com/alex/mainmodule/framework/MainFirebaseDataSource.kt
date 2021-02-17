package com.alex.mainmodule.framework

import androidx.lifecycle.LiveData
import com.alex.mainmodule.data.LocalDataSource
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.utils.AESCrypt
import com.alex.mainmodule.utils.AESCrypt.decrypt
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kiwimob.firestore.livedata.livedata


interface MainFirebaseDataSource {
    fun addRestaurant(restaurant: Restaurant): Task<DocumentReference>
    fun getRestaurantsListLiveData(): LiveData<List<Restaurant>>
    fun getUsersListLiveData(): LiveData<List<User>>
    fun addUser(user: User): Task<Void>
    fun editReview(restaurant: Restaurant, oldReview: Review, newReview: Review)
    fun deleteReview(restaurant: Restaurant, review: Review): Task<Void>
    fun deleteRestaurant(restaurant: Restaurant): Task<Void>
    fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant)
    fun deleteUser(user: User): Task<Void>
    fun isUserInDataSource(email: String): Boolean
    fun addReview(review: Review, restaurant: Restaurant)
    fun editUser(oldUser: User, newUser: User): Any
    fun getCurrentUser(): User
    fun isUserAlreadyLoggedIn(): Boolean
    fun getUserFromDataSource(email: String): Task<DocumentSnapshot>
    fun authenticateUser(user: User): Boolean
    fun logout(): Boolean
    fun clearFirebaseDataSource()
}

class MainFirebaseDataSourceImpl(
    private var localDataSource: LocalDataSource,
    firestore: FirebaseFirestore
) : MainFirebaseDataSource {

    private val restaurantsReference = firestore.collection("restaurants")
    private val usersReference = firestore.collection("users")
    private val reviewsFieldName = "reviews"

    override fun addRestaurant(restaurant: Restaurant) = restaurantsReference.add(restaurant)

    override fun getRestaurantsListLiveData() =
        restaurantsReference.livedata(Restaurant::class.java)

    override fun addReview(review: Review, restaurant: Restaurant) {
        review.dateInMillis = System.currentTimeMillis()

        restaurantsReference.document(restaurant.id)
            .update(reviewsFieldName, FieldValue.arrayUnion(review))
    }

    override fun editUser(oldUser: User, newUser: User) {
        //password hasn't changed
        if (newUser.password.isEmpty()) {
            newUser.password = decrypt(oldUser.password)
        }

        //the email changed
        if (localDataSource.getCurrentUser().email == oldUser.email) {
            localDataSource.authenticateUser(newUser)
        }

        addUser(newUser)
        if (oldUser.email != newUser.email) {
            replaceAllOccurrencesForOldEmail(oldUser.email, newUser.email)
            deleteUser(oldUser)
        }
    }

    private fun replaceAllOccurrencesForOldEmail(oldEmail: String, newEmail: String) {
        updateOwnerForRestaurants(oldEmail, newEmail)
        updateAuthorForReviews(oldEmail, newEmail)
    }

    //WARNING! this will delete everything including current user or restaurant
    override fun clearFirebaseDataSource() {
        restaurantsReference
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val restaurant = document.toObject(Restaurant::class.java)
                    deleteRestaurant(restaurant)
                }
            }

        usersReference
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    deleteUser(user)
                }
            }
    }

    private fun updateOwnerForRestaurants(oldOwnerEmail: String, newOwnerEmail: String) {
        restaurantsReference
            .whereEqualTo("ownerEmail", oldOwnerEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val restaurant = document.toObject(Restaurant::class.java)
                    val newRestaurant = restaurant.copy()
                    newRestaurant.ownerEmail = newOwnerEmail
                    editRestaurant(restaurant, newRestaurant)
                }
            }
    }

    private fun updateAuthorForReviews(oldAuthorEmail: String, newAuthorEmail: String) {
        restaurantsReference
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val restaurant = document.toObject(Restaurant::class.java)
                    val newRestaurant = restaurant.copy()
                    newRestaurant.reviews.forEach {
                        if (it.userEmail == oldAuthorEmail) {
                            it.userEmail = newAuthorEmail
                        }
                    }

                    editRestaurant(restaurant, newRestaurant)
                }
            }
    }

    override fun getCurrentUser(): User = localDataSource.getCurrentUser()

    override fun isUserAlreadyLoggedIn() = localDataSource.isUserAlreadyLoggedIn()

    override fun getUsersListLiveData() = usersReference.livedata(User::class.java)

    override fun addUser(user: User): Task<Void> {
        user.password = AESCrypt.encrypt(user.password)
        return usersReference.document(user.email).set(user)
    }

    override fun editReview(restaurant: Restaurant, oldReview: Review, newReview: Review) {
        newReview.dateInMillis = oldReview.dateInMillis
        if (newReview.userEmail.isEmpty()) {
            newReview.userEmail = oldReview.userEmail
        }
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
        newRestaurant.picture = oldRestaurant.picture
        restaurantsReference.document(oldRestaurant.id).set(newRestaurant)
    }

    override fun isUserInDataSource(email: String) =
        usersReference.document(email).get().result?.exists() == true

    override fun getUserFromDataSource(email: String) =
        usersReference.document(email).get()

    override fun authenticateUser(user: User) =
        localDataSource.authenticateUser(user)

    override fun logout() = localDataSource.logout()

    override fun deleteUser(user: User) = usersReference.document(user.email).delete()

}