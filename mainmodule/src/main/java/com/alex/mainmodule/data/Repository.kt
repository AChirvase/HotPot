package com.alex.mainmodule.data

import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.framework.MainFirebaseDataSource
import com.alex.mainmodule.framework.local_datasource.LocalDataSource
import org.koin.core.KoinComponent


class Repository(
    private val firebaseDataSource: MainFirebaseDataSource,
    private val localDataSource: LocalDataSource
) : KoinComponent {
    fun addRestaurant(restaurant: Restaurant) = firebaseDataSource.addRestaurant(restaurant)

    fun getRestaurantsLiveData() = firebaseDataSource.getRestaurantsListLiveData()

    fun addReview(review: Review, restaurant: Restaurant) =
        firebaseDataSource.addReview(review, restaurant, localDataSource.getCurrentUser().email)

    fun getUsersListLiveData() = firebaseDataSource.getUsersListLiveData()

    fun addUser(user: User) = firebaseDataSource.addUser(user)

    fun editReview(restaurant: Restaurant, oldReview: Review, newReview: Review) =
        firebaseDataSource.editReview(restaurant, oldReview, newReview)

    fun deleteReview(restaurant: Restaurant, review: Review) =
        firebaseDataSource.deleteReview(restaurant, review)

    fun deleteRestaurant(restaurant: Restaurant) =
        firebaseDataSource.deleteRestaurant(restaurant)


    fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant) =
        firebaseDataSource.editRestaurant(oldRestaurant, newRestaurant)

    fun editUser(oldUser: User, newUser: User) =
        firebaseDataSource.editUser(oldUser, newUser)

    fun deleteUser(user: User) = firebaseDataSource.deleteUser(user)

    fun isUserAlreadyAuthenticated() = localDataSource.isUserAlreadyLoggedIn()

    fun getCurrentUser() = localDataSource.getCurrentUser()

}