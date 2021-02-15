package com.alex.mainmodule.framework.remote_datasource

import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.User
import retrofit2.Response
import retrofit2.http.GET

interface HotPotService {

    @GET("/b/O193")
    suspend fun getUsersList(): List<User>

    @GET("/b/6R6S")
    suspend fun getRestaurantsList(): Response<List<Restaurant>>
}