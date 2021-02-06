package com.alex.mainmodule.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import org.koin.core.KoinComponent


sealed class MainActivityViewState {
    object ViewRestaurantsList : MainActivityViewState()
    object ViewRestaurant : MainActivityViewState()
    object ExitApp : MainActivityViewState()
}

class MainActivityViewModel(
    private val context: Context,
    private val repository: Repository
) : ViewModel(), KoinComponent {


    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    val viewState: MutableLiveData<MainActivityViewState> by lazy {
        MutableLiveData<MainActivityViewState>().also {
            it.value = MainActivityViewState.ViewRestaurantsList
        }
    }

    val restaurantsListLiveData: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>()
    }

    val selectedRestaurantLiveData: MutableLiveData<Restaurant> by lazy {
        MutableLiveData<Restaurant>()
    }

    init {
        repository.getRestaurantsLiveData().observeForever {
            restaurantsListLiveData.value = it
        }
    }

    fun onBackPressed() {
        when (viewState.value) {
            MainActivityViewState.ViewRestaurantsList -> viewState.value =
                MainActivityViewState.ExitApp
            else -> {
            }
        }
    }

    fun onRestaurantCollapsed() {
        viewState.value = MainActivityViewState.ViewRestaurantsList
    }

    fun goToViewRestaurant(restaurant: Restaurant) {
        selectedRestaurantLiveData.value = restaurant
        viewState.value = MainActivityViewState.ViewRestaurant
    }

    fun addRestaurantsTest() {
        repository.addRestaurant(Restaurant(name = "KFC"))
        repository.addRestaurant(Restaurant(name = "MCDonald"))
        repository.addRestaurant(Restaurant(name = "Shaworma"))
    }

}