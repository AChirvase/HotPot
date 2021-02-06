package com.alex.mainmodule.data

import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.framework.FirebaseDataSource
import org.koin.core.KoinComponent


class Repository(
    private val firebaseDataSource: FirebaseDataSource,
) : KoinComponent {
    fun addRestaurant(restaurant: Restaurant) = firebaseDataSource.addRestaurant(restaurant)
    fun getRestaurantsLiveData() = firebaseDataSource.getRestaurantsLiveData()
    fun addReview(review: Review, restaurant: Restaurant) =
        firebaseDataSource.addReview(review, restaurant)


}