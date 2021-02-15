package com.alex.mainmodule.framework.remote_datasource

import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.User
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getRestaurantsList(): Response<List<Restaurant>>
    suspend fun getUsersList(): List<User>
}

class RemoteDataSourceImpl(private val service: HotPotService) :
    RemoteDataSource {

    override suspend fun getUsersList() = service.getUsersList()


    override suspend fun getRestaurantsList() = service.getRestaurantsList()

}