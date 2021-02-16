package com.alex.mainmodule.data

import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.User
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getRestaurantsList(): Response<List<Restaurant>>
    suspend fun getUsersList(): List<User>
}