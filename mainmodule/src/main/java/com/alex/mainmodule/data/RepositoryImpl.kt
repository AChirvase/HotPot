package com.alex.mainmodule.data

import androidx.lifecycle.LiveData
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.framework.MainFirebaseDataSource
import com.alex.mainmodule.framework.local_datasource.LocalDataSource
import com.alex.mainmodule.framework.remote_datasource.RemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import org.koin.core.KoinComponent
import retrofit2.Response


interface Repository {

    fun addRestaurant(restaurant: Restaurant): Task<DocumentReference>
    fun getRestaurantsLiveData(): LiveData<List<Restaurant>>
    fun addReview(review: Review, restaurant: Restaurant)
    fun getUsersListLiveData(): LiveData<List<User>>
    fun addUser(user: User): Task<Void>
    fun editReview(restaurant: Restaurant, oldReview: Review, newReview: Review)
    fun deleteReview(restaurant: Restaurant, review: Review): Task<Void>
    fun deleteRestaurant(restaurant: Restaurant): Task<Void>
    fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant)
    fun editUser(oldUser: User, newUser: User): Any
    fun deleteUser(user: User): Task<Void>
    fun isUserAlreadyAuthenticated(): Boolean
    fun getCurrentUser(): User
    fun updateCurrentUserData(user: User): Boolean
    fun clearFirebaseDataSource()
    suspend fun getUsersList(): List<User>
    suspend fun getRestaurantsList(): Response<List<Restaurant>>

}

class RepositoryImpl(
    private val firebaseDataSource: MainFirebaseDataSource,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : Repository, KoinComponent {
    override fun addRestaurant(restaurant: Restaurant) =
        firebaseDataSource.addRestaurant(restaurant)

    override fun getRestaurantsLiveData() = firebaseDataSource.getRestaurantsListLiveData()

    override fun addReview(review: Review, restaurant: Restaurant) =
        firebaseDataSource.addReview(review, restaurant)

    override fun getUsersListLiveData() = firebaseDataSource.getUsersListLiveData()

    override fun addUser(user: User) = firebaseDataSource.addUser(user)

    override fun editReview(restaurant: Restaurant, oldReview: Review, newReview: Review) =
        firebaseDataSource.editReview(restaurant, oldReview, newReview)

    override fun deleteReview(restaurant: Restaurant, review: Review) =
        firebaseDataSource.deleteReview(restaurant, review)

    override fun deleteRestaurant(restaurant: Restaurant) =
        firebaseDataSource.deleteRestaurant(restaurant)


    override fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant) =
        firebaseDataSource.editRestaurant(oldRestaurant, newRestaurant)

    override fun editUser(oldUser: User, newUser: User) =
        firebaseDataSource.editUser(oldUser, newUser)

    override fun deleteUser(user: User) = firebaseDataSource.deleteUser(user)

    override fun isUserAlreadyAuthenticated() = localDataSource.isUserAlreadyLoggedIn()

    override fun getCurrentUser() = localDataSource.getCurrentUser()

    override fun updateCurrentUserData(user: User) =
        user.let { localDataSource.authenticateUser(it) }

    override fun clearFirebaseDataSource() = firebaseDataSource.clearFirebaseDataSource()

    override suspend fun getUsersList() = remoteDataSource.getUsersList()

    override suspend fun getRestaurantsList() = remoteDataSource.getRestaurantsList()
}