package com.alex.mainmodule.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alex.mainmodule.R
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import org.koin.core.KoinComponent


sealed class MainActivityViewState {
    object ShowRestaurantsList : MainActivityViewState()
    object ShowUsersList : MainActivityViewState()
    object ShowRestaurant : MainActivityViewState()
    object ShowAddReviewScreen : MainActivityViewState()
    object ShowAddRestaurantScreen : MainActivityViewState()
    object AddRestaurant : MainActivityViewState()
    object AddReview : MainActivityViewState()
    object ShowEditReviewScreen : MainActivityViewState()
    object ShowEditRestaurantScreen : MainActivityViewState()
    object EditRestaurant : MainActivityViewState()
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
            it.value = MainActivityViewState.ShowRestaurantsList
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
            restaurantsListLiveData.value = it.sortedBy { restaurant -> restaurant.name }.toList()
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
            MainActivityViewState.ShowRestaurantsList -> MainActivityViewState.ExitApp
            MainActivityViewState.ShowUsersList -> MainActivityViewState.ShowRestaurantsList
            MainActivityViewState.ShowEditReviewScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowEditRestaurantScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowAddReviewScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowAddRestaurantScreen -> MainActivityViewState.ShowRestaurantsList
            MainActivityViewState.ShowRestaurant -> MainActivityViewState.ShowRestaurantsList
            else -> viewState.value
        }
    }

    fun onRestaurantCollapsed() {
        viewState.value = MainActivityViewState.ShowRestaurantsList
    }

    fun goToViewRestaurant(restaurant: Restaurant) {
        selectedRestaurantLiveData.value = restaurant
        viewState.value = MainActivityViewState.ShowRestaurant
    }

    private fun addRestaurantsTest() {
        repository.addRestaurant(Restaurant(name = "KFC"))
        repository.addRestaurant(Restaurant(name = "MCDonald"))
        repository.addRestaurant(Restaurant(name = "Shaworma"))
    }


    private fun signOut() {
        viewState.value = MainActivityViewState.FinishActivity
        loginNavigator.signOut()
    }

    fun onUserClicked(user: User) {

    }

    fun addReview(review: Review) {
        selectedRestaurantLiveData.value?.let { repository.addReview(review, it) }
        viewState.value = MainActivityViewState.ShowRestaurant
    }

    private fun switchBetweenUsersAndRestaurantsList() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ShowRestaurantsList -> MainActivityViewState.ShowUsersList
            MainActivityViewState.ShowUsersList -> MainActivityViewState.ShowRestaurantsList
            else -> viewState.value
        }
    }

    fun onMainButtonClicked() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ShowRestaurant -> MainActivityViewState.ShowAddReviewScreen
            MainActivityViewState.ShowAddReviewScreen -> MainActivityViewState.AddReview
            MainActivityViewState.ShowAddRestaurantScreen -> MainActivityViewState.AddRestaurant
            MainActivityViewState.ShowEditReviewScreen -> MainActivityViewState.EditReview
            MainActivityViewState.ShowEditRestaurantScreen -> MainActivityViewState.EditRestaurant
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
        viewState.value = MainActivityViewState.ShowRestaurant
    }

    fun deleteReview() {
        val restaurant = selectedRestaurantLiveData.value
        val review = selectedReviewLiveData.value
        if (restaurant == null || review == null) {
            return
        }
        repository.deleteReview(restaurant, review)
        viewState.value = MainActivityViewState.ShowRestaurant
    }

    fun deleteRestaurant() {
        selectedRestaurantLiveData.value?.let { repository.deleteRestaurant(it) }
        viewState.value = MainActivityViewState.ShowRestaurantsList
    }

    fun addRestaurant(restaurant: Restaurant) {
        repository.addRestaurant(restaurant)
        viewState.value = MainActivityViewState.ShowRestaurantsList
    }

    fun editRestaurant(newRestaurant: Restaurant) {
        selectedRestaurantLiveData.value?.let { repository.editRestaurant(it, newRestaurant) }
        viewState.value = MainActivityViewState.ShowRestaurant
    }

    fun onBottomAppBarLeftBtnClicked() {
        when (viewState.value) {
            MainActivityViewState.ShowRestaurantsList -> signOut()
            else -> onBackPressed()
        }
    }


    fun onBottomAppBarMiddleRightBtnClicked() {
        when (viewState.value) {
            MainActivityViewState.ShowRestaurantsList ->
                viewState.value = MainActivityViewState.ShowAddRestaurantScreen
            MainActivityViewState.ShowRestaurant -> deleteRestaurant()
            else -> {
            }
        }

    }

    fun onBottomAppBarFarRightBtn2Clicked() {
        when (viewState.value) {
            MainActivityViewState.ShowRestaurant -> viewState.value =
                MainActivityViewState.ShowEditRestaurantScreen
            else -> {
                switchBetweenUsersAndRestaurantsList()
            }
        }
    }

    fun getImageForMainBtn() = when (viewState.value) {
        MainActivityViewState.ShowRestaurantsList,
        MainActivityViewState.ShowUsersList -> R.drawable.ic_baseline_search_24
        MainActivityViewState.ShowRestaurant -> R.drawable.ic_baseline_rate_review_24
        MainActivityViewState.ShowEditReviewScreen -> R.drawable.ic_baseline_mode_edit_24
        MainActivityViewState.ShowAddReviewScreen -> R.drawable.ic_baseline_send_24
        MainActivityViewState.ShowAddRestaurantScreen -> R.drawable.ic_baseline_add_restaurant_24
        MainActivityViewState.ShowEditRestaurantScreen -> R.drawable.ic_baseline_mode_edit_24
        else -> R.drawable.ic_baseline_search_24
    }

    fun getImageForFarRightBtn() = when (viewState.value) {
        MainActivityViewState.ShowRestaurantsList -> R.drawable.ic_baseline_groups_24
        MainActivityViewState.ShowRestaurant -> R.drawable.ic_baseline_mode_edit_24
        MainActivityViewState.ShowUsersList -> R.drawable.ic_baseline_restaurant_24
        else -> null
    }

    fun getImageForMiddleRightBtn() = when (viewState.value) {
        MainActivityViewState.ShowRestaurantsList -> R.drawable.ic_baseline_add_restaurant_24
        MainActivityViewState.ShowRestaurant -> R.drawable.ic_baseline_delete_24
        MainActivityViewState.ShowUsersList -> R.drawable.ic_baseline_person_add_alt_1_24
        else -> null
    }

    fun getImageForLeftBtn() = when (viewState.value) {
        MainActivityViewState.ShowRestaurantsList -> R.drawable.ic_baseline_logout_24
        else -> R.drawable.ic_baseline_chevron_left_24
    }


}

interface LoginNavigator {
    fun signOut()
}