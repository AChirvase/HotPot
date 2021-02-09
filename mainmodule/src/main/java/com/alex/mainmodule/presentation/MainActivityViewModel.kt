package com.alex.mainmodule.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import org.koin.core.KoinComponent


sealed class MainActivityViewState {
    object ViewRestaurantsList : MainActivityViewState()
    object ViewUsersList : MainActivityViewState()
    object ViewRestaurant : MainActivityViewState()
    object ShowWriteReviewScreen : MainActivityViewState()
    object SendReview : MainActivityViewState()
    object ShowEditReviewScreen : MainActivityViewState()
    object EditReview : MainActivityViewState()
    object ExitApp : MainActivityViewState()
    object FinishActivity : MainActivityViewState()
}

class MainActivityViewModel(
    private val context: Context,
    private val repository: Repository,
    private val loginNavigator: LoginNavigator
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

    val usersListLiveData: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>()
    }

    val selectedRestaurantLiveData: MutableLiveData<Restaurant> by lazy {
        MutableLiveData<Restaurant>()
    }

    val selectedReviewLiveData: MutableLiveData<Review> by lazy {
        MutableLiveData<Review>()
    }

    init {
        repository.getRestaurantsLiveData().observeForever {
            restaurantsListLiveData.value = it
            selectedRestaurantLiveData.value = it.find { restaurant ->
                restaurant.id == selectedRestaurantLiveData.value?.id
            }
        }

        repository.getUsersListLiveData().observeForever {
            usersListLiveData.value = it
        }
    }

    fun onBackPressed() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ViewRestaurantsList -> MainActivityViewState.ExitApp
            MainActivityViewState.ViewUsersList -> MainActivityViewState.ExitApp
            MainActivityViewState.ShowEditReviewScreen -> MainActivityViewState.ViewRestaurant
            MainActivityViewState.ShowWriteReviewScreen -> MainActivityViewState.ViewRestaurant
            MainActivityViewState.ViewRestaurant -> MainActivityViewState.ViewRestaurantsList
            else -> viewState.value
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

    fun signOut() {
        viewState.value = MainActivityViewState.FinishActivity
        loginNavigator.signOut()
    }

    fun onUserClicked(specific: User) {

    }

    fun addReview(review: Review) {
        selectedRestaurantLiveData.value?.let { repository.addReview(review, it) }
        viewState.value = MainActivityViewState.ViewRestaurant
    }

    fun onSwitchUsersRestaurantsPressed() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ViewRestaurantsList -> MainActivityViewState.ViewUsersList
            MainActivityViewState.ViewRestaurant -> MainActivityViewState.ViewUsersList
            MainActivityViewState.ViewUsersList -> MainActivityViewState.ViewRestaurantsList
            else -> viewState.value
        }
    }

    fun onMainButtonClicked() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ViewRestaurant -> MainActivityViewState.ShowWriteReviewScreen
            MainActivityViewState.ShowWriteReviewScreen -> MainActivityViewState.SendReview
            MainActivityViewState.ShowEditReviewScreen -> MainActivityViewState.EditReview
            else -> viewState.value
        }
    }

    fun showEditReviewScreen(review: Review) {
        selectedReviewLiveData.value = review
        viewState.value = MainActivityViewState.ShowEditReviewScreen
    }

    fun editReview(newReview: Review) {
        val restaurant = selectedRestaurantLiveData.value
        val oldReview = selectedReviewLiveData.value
        if (restaurant == null || oldReview == null) {
            return
        }
        repository.replaceReview(restaurant, oldReview, newReview)
        viewState.value = MainActivityViewState.ViewRestaurant
    }

    fun deleteReview() {
        val restaurant = selectedRestaurantLiveData.value
        val review = selectedReviewLiveData.value
        if (restaurant == null || review == null) {
            return
        }
        repository.deleteReview(restaurant, review)
        viewState.value = MainActivityViewState.ViewRestaurant
    }
}

interface LoginNavigator {
    fun signOut()
}