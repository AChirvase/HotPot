package com.alex.mainmodule.data

import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.framework.FirebaseDataSource
import org.koin.core.KoinComponent


class Repository(
    private val firebaseDataSource: FirebaseDataSource
) : KoinComponent {
    fun addRestaurant(restaurant: Restaurant) = firebaseDataSource.addRestaurant(restaurant)

    fun getRestaurantsLiveData() = firebaseDataSource.getRestaurantsListLiveData()

    fun addReview(review: Review, restaurant: Restaurant) =
        firebaseDataSource.addReview(review, restaurant)

    fun getUsersListLiveData() = firebaseDataSource.getUsersListLiveData()

    fun addUser(user: User) = firebaseDataSource.addUser(user)

    fun replaceReview(restaurant: Restaurant, oldReview: Review, newReview: Review) =
        firebaseDataSource.replaceReview(restaurant, oldReview, newReview)

    fun deleteReview(restaurant: Restaurant, review: Review) =
        firebaseDataSource.deleteReview(restaurant, review)

    fun deleteRestaurant(restaurant: Restaurant) =
        firebaseDataSource.deleteRestaurant(restaurant)


    fun editRestaurant(oldRestaurant: Restaurant, newRestaurant: Restaurant) =
        firebaseDataSource.editRestaurant(oldRestaurant, newRestaurant)

}