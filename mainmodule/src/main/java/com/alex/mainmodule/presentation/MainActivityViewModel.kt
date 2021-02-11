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
    object ShowAddUserScreen : MainActivityViewState()
    object AddRestaurant : MainActivityViewState()
    object AddReview : MainActivityViewState()
    object AddUser : MainActivityViewState()
    object ShowEditReviewScreen : MainActivityViewState()
    object ShowEditRestaurantScreen : MainActivityViewState()
    object ShowEditUserScreen : MainActivityViewState()
    object EditRestaurant : MainActivityViewState()
    object EditReview : MainActivityViewState()
    object EditUser : MainActivityViewState()
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

    val selectedUserLiveData: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
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
            selectedUserLiveData.value = it.find { user ->
                user.email == selectedUserLiveData.value?.email
            }
        }
    }

    fun onBackPressed() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ShowRestaurantsList -> MainActivityViewState.ExitApp
            MainActivityViewState.ShowUsersList -> MainActivityViewState.ShowRestaurantsList
            MainActivityViewState.ShowEditReviewScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowEditRestaurantScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowEditUserScreen -> MainActivityViewState.ShowUsersList
            MainActivityViewState.ShowAddReviewScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowAddRestaurantScreen -> MainActivityViewState.ShowRestaurantsList
            MainActivityViewState.ShowAddUserScreen -> MainActivityViewState.ShowUsersList
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

    fun showEditUserScreen(user: User) {
        selectedUserLiveData.value = user
        viewState.value = MainActivityViewState.ShowEditUserScreen
    }

    private fun addRestaurantsTest() {
        repository.addRestaurant(Restaurant(name = "KFC"))
        repository.addRestaurant(Restaurant(name = "MCDonald"))
        repository.addRestaurant(Restaurant(name = "Shaworma"))
    }


    private fun logout() {
        viewState.value = MainActivityViewState.FinishActivity
        loginNavigator.logout()
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
            MainActivityViewState.ShowAddUserScreen -> MainActivityViewState.AddUser
            MainActivityViewState.ShowEditUserScreen -> MainActivityViewState.EditUser
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
        repository.editReview(restaurant, oldReview, newReview)
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
            MainActivityViewState.ShowRestaurantsList -> logout()
            else -> onBackPressed()
        }
    }


    fun onBottomAppBarMiddleRightBtnClicked() {
        when (viewState.value) {
            MainActivityViewState.ShowRestaurantsList ->
                viewState.value = MainActivityViewState.ShowAddRestaurantScreen
            MainActivityViewState.ShowRestaurant -> deleteRestaurant()
            MainActivityViewState.ShowUsersList ->
                viewState.value = MainActivityViewState.ShowAddUserScreen
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
        MainActivityViewState.ShowAddUserScreen -> R.drawable.ic_baseline_person_add_alt_1_24
        MainActivityViewState.ShowEditUserScreen -> R.drawable.ic_baseline_mode_edit_24
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

    fun deleteUser() {
        selectedUserLiveData.value?.let {
            repository.deleteUser(it)

            if (it.email == repository.getCurrentUser().email) {
                logout()
                return
            }
        }
        viewState.value = MainActivityViewState.ShowUsersList
    }

    fun addUser(user: User) {
        repository.addUser(user)
        viewState.value = MainActivityViewState.ShowUsersList
    }

    fun editUser(newUser: User) {
        val oldUser = selectedUserLiveData.value ?: return
        repository.editUser(oldUser, newUser)
        viewState.value = MainActivityViewState.ShowUsersList
    }


}

interface LoginNavigator {
    fun logout()
}