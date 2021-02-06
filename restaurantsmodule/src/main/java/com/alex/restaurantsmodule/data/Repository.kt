package com.alex.restaurantsmodule.data

import com.alex.restaurantsmodule.FirebaseDataSource
import com.alex.restaurantsmodule.domain.Restaurant
import com.alex.restaurantsmodule.domain.Review
import org.koin.core.KoinComponent


class Repository(
    private val firebaseDataSource: FirebaseDataSource,
) : KoinComponent {

    fun addRestaurant(restaurant: Restaurant) = firebaseDataSource.addRestaurant(restaurant)
    fun getRestaurantsLiveData() = firebaseDataSource.getRestaurantsLiveData()
    fun addReview(review: Review, restaurant: Restaurant) =
        firebaseDataSource.addReview(review, restaurant)

}