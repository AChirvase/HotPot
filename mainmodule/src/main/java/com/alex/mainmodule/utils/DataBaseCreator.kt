package com.alex.mainmodule.utils

import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant

object DataBaseCreator {

    fun addRestaurantsTest(repository: Repository) {
        repository.addRestaurant(Restaurant(name = "KFC"))
        repository.addRestaurant(Restaurant(name = "MCDonald"))
        repository.addRestaurant(Restaurant(name = "Shaworma"))
    }
}