package com.alex.mainmodule.framework.remote_datasource

import com.alex.mainmodule.data.RemoteDataSource

class RemoteDataSourceImpl(private val service: HotPotService) :
    RemoteDataSource {

    override suspend fun getUsersList() = service.getUsersList()
    override suspend fun getRestaurantsList() = service.getRestaurantsList()

}