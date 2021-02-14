package com.alex.mainmodule.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alex.mainmodule.R
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.utils.TestDataSourceObjects.createDataBaseTest
import org.koin.core.KoinComponent
import java.util.*


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
    object FocusOnSearchRestaurant : MainActivityViewState()
    object FocusOnSearchUser : MainActivityViewState()
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

    val filteredRestaurantsListLiveData: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>()
    }

    val usersListLiveData: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>()
    }

    val filteredUsersListLiveData: MutableLiveData<List<User>> by lazy {
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
        repository.getRestaurantsLiveData().observeForever { restaurantList ->

            //calculate restaurant rating
            restaurantList.map { restaurant ->
                restaurant.rating = if (restaurant.reviews.isNullOrEmpty()) {
                    0f
                } else {
                    restaurant.reviews.map { review -> review.restaurantOverallEvaluation }
                        .average().toFloat()
                }
            }

            //sort by rating
            restaurantsListLiveData.value = restaurantList.sortedByDescending { it.rating }.toList()

            filterRestaurants()
            //update the current selected restaurant
            selectedRestaurantLiveData.value = restaurantList.find { restaurant ->
                restaurant.id == selectedRestaurantLiveData.value?.id
            }
        }

        repository.getUsersListLiveData().observeForever {
            usersListLiveData.value = it
            filterRestaurants()
            filteredUsersListLiveData.value = usersListLiveData.value
            it.find { user ->
                user.email == repository.getCurrentUser().email
            }.also { user ->
                if (user == null) {
                    logout()
                } else {
                    repository.updateCurrentUserData(user)
                }
            }

            selectedUserLiveData.value = it.find { user ->
                user.email == selectedUserLiveData.value?.email
            }
        }
    }

    private fun filterRestaurants() {
        filteredRestaurantsListLiveData.value = if (getUserRole() == Role.OWNER) {
            restaurantsListLiveData.value?.filter { it.ownerEmail == repository.getCurrentUser().email }
                ?.toList()
        } else {
            restaurantsListLiveData.value?.sortedByDescending { it.rating }?.toList()
        }
    }

    fun onBackPressed() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.FocusOnSearchRestaurant -> MainActivityViewState.ShowRestaurantsList
            MainActivityViewState.FocusOnSearchUser -> MainActivityViewState.ShowUsersList
            MainActivityViewState.ShowRestaurantsList -> MainActivityViewState.ExitApp
            MainActivityViewState.ShowUsersList -> MainActivityViewState.ShowRestaurantsList
            MainActivityViewState.ShowEditReviewScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowEditRestaurantScreen -> MainActivityViewState.ShowRestaurant
            MainActivityViewState.ShowEditUserScreen -> {
                if (getUserRole() == Role.ADMIN)
                    MainActivityViewState.ShowUsersList
                else
                    MainActivityViewState.ShowRestaurantsList
            }
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

    private fun showEditReviewScreen() {
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

    private fun deleteReview() {
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

    fun onMainButtonClicked() {
        viewState.value = when (viewState.value) {
            MainActivityViewState.ShowRestaurantsList -> {
                if (getUserRole() == Role.OWNER) {
                    MainActivityViewState.ShowAddRestaurantScreen
                } else {
                    MainActivityViewState.FocusOnSearchRestaurant
                }
            }
            MainActivityViewState.ShowUsersList -> MainActivityViewState.FocusOnSearchUser
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

    fun onBottomAppBarFarRightBtnClicked() {
        when (viewState.value) {
            MainActivityViewState.ShowRestaurant -> viewState.value =
                MainActivityViewState.ShowEditRestaurantScreen
            else -> {
                if (getUserRole() == Role.ADMIN) {
                    switchBetweenUsersAndRestaurantsList()
                    filterRestaurantsByName("")
                    filterUsersByEmail("")
                } else {
                    showEditUserScreen(repository.getCurrentUser())
                }
            }
        }
    }

    fun onSearchBarTextChanged(searchSequence: String) {
        when (viewState.value) {
            MainActivityViewState.FocusOnSearchRestaurant,
            MainActivityViewState.ShowRestaurantsList -> filterRestaurantsByName(searchSequence)
            MainActivityViewState.FocusOnSearchUser,
            MainActivityViewState.ShowUsersList -> filterUsersByEmail(searchSequence)
            else -> {
            }
        }


    }


    fun getImageForMainBtn() = when (viewState.value) {
        MainActivityViewState.ShowRestaurantsList,
        MainActivityViewState.ShowUsersList -> {
            if (getUserRole() == Role.OWNER) {
                R.drawable.ic_baseline_add_restaurant_24
            } else {
                R.drawable.ic_baseline_search_24
            }
        }
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
        MainActivityViewState.ShowRestaurantsList -> {
            if (getUserRole() == Role.ADMIN) {
                R.drawable.ic_baseline_groups_24
            } else {
                R.drawable.ic_baseline_person_24
            }
        }
        MainActivityViewState.ShowRestaurant -> R.drawable.ic_baseline_mode_edit_24
        MainActivityViewState.ShowUsersList -> R.drawable.ic_baseline_restaurant_24
        else -> null
    }

    fun getImageForMiddleRightBtn() = when (viewState.value) {
        MainActivityViewState.ShowRestaurantsList -> {
            if (getUserRole() == Role.ADMIN)
                R.drawable.ic_baseline_add_restaurant_24
            else
                null
        }
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
        viewState.value = if (getUserRole() == Role.ADMIN) {
            MainActivityViewState.ShowUsersList
        } else {
            MainActivityViewState.ShowRestaurantsList
        }
    }

    private fun filterRestaurantsByName(searchSequence: String) {
        filteredRestaurantsListLiveData.value =
            restaurantsListLiveData.value?.filter {
                it.name.toLowerCase(Locale.getDefault())
                    .contains(searchSequence.toLowerCase(Locale.getDefault()))
            }
    }

    private fun filterUsersByEmail(searchSequence: String) {
        filteredUsersListLiveData.value =
            usersListLiveData.value?.filter {
                it.email.toLowerCase(Locale.getDefault())
                    .contains(searchSequence.toLowerCase(Locale.getDefault()))
            }
    }

    fun filterRestaurantsByRating(minValue: Float, maxValue: Float) {
        filteredRestaurantsListLiveData.value =
            restaurantsListLiveData.value?.filter {
                it.rating in minValue..maxValue
            }
    }

    fun onDeleteBtnPressed() {
        when (viewState.value) {
            MainActivityViewState.ShowEditReviewScreen -> {
                if (getUserRole() == Role.OWNER) {
                    deleteReply()
                } else {
                    deleteReview()
                }
            }

            else -> {
            }
        }


    }

    private fun deleteReply() {
        val restaurant = selectedRestaurantLiveData.value
        val oldReview = selectedReviewLiveData.value
        if (restaurant == null || oldReview == null) {
            return
        }
        var newReview = oldReview.copy()
        newReview.reply = ""

        repository.editReview(restaurant, oldReview, newReview)
        viewState.value = MainActivityViewState.ShowRestaurant
    }

    fun getUserRole() = Role.valueOf(repository.getCurrentUser().role)

    fun onReviewClicked(review: Review) {
        selectedReviewLiveData.value = review
        showEditReviewScreen()
    }

    fun goToAddReply(review: Review) {
        val restaurant = restaurantsListLiveData.value?.find { it.reviews.contains(review) }
        selectedRestaurantLiveData.value = restaurant
        selectedReviewLiveData.value = review
        viewState.value = MainActivityViewState.ShowEditReviewScreen
    }

    fun createDataBase() = createDataBaseTest(repository)
    fun clearDataBase() = repository.clearFirebaseDataSource()

}

interface LoginNavigator {
    fun logout()
}