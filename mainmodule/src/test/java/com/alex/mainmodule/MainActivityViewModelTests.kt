package com.alex.mainmodule

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.presentation.LoginNavigator
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.rules.TestRule
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainActivityViewModelTests : Application(), KoinTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val viewModel: MainActivityViewModel by inject()
    private val repository: Repository by inject()

    private val mainModuleTest = module {
        single { mock(Repository::class.java) }
        single { mock(LoginNavigator::class.java) }

        viewModel {
            MainActivityViewModel(
                androidContext(),
                get(),
                get()
            )
        }
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        startKoin {
            androidContext(this@MainActivityViewModelTests)
            modules(mainModuleTest)
        }
        testCoroutineRule.runBlockingTest {
            doReturn(MutableLiveData<List<User>>())
                .`when`(repository)
                .getUsersListLiveData()
        }
        testCoroutineRule.runBlockingTest {
            doReturn(MutableLiveData<List<Restaurant>>())
                .`when`(repository)
                .getRestaurantsLiveData()
        }
        testCoroutineRule.runBlockingTest {
            doReturn(User("User Name", "user@email.com", "1234", Role.ADMIN.name))
                .`when`(repository)
                .getCurrentUser()
        }
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun switch_to_users_list_when_in_restaurants_list() {
        //GIVEN
        viewModel.viewState.value = MainActivityViewState.ShowRestaurantsList
        //WHEN
        viewModel.switchBetweenUsersAndRestaurantsList()
        //THEN
        assertEquals(MainActivityViewState.ShowUsersList, viewModel.viewState.value)

    }

    @Test
    fun switch_to_restaurants_list_when_in_users_list() {
        //GIVEN
        viewModel.viewState.value = MainActivityViewState.ShowUsersList
        //WHEN
        viewModel.switchBetweenUsersAndRestaurantsList()
        //THEN
        assertEquals(MainActivityViewState.ShowRestaurantsList, viewModel.viewState.value)
    }

    @Test
    fun state_to_show_users_list_after_delete_user() {
        //WHEN
        viewModel.deleteUser()

        //THEN
        assertEquals(MainActivityViewState.ShowUsersList, viewModel.viewState.value)
    }

    @Test
    fun main_activity_finish_after_delete_current_user() {
        //GIVEN
        var user = User(email = "test@test.com")
        testCoroutineRule.runBlockingTest {
            doReturn(user)
                .`when`(repository)
                .getCurrentUser()
        }
        viewModel.selectedUserLiveData.value = user

        //WHEN
        viewModel.deleteUser()
        //THEN
        assertEquals(MainActivityViewState.FinishActivity, viewModel.viewState.value)
    }

    @Test
    fun filtered_restaurants_list_does_not_contain_sequence() {
        filtered_restaurants_does_not_contain_sequence("res")
        filtered_restaurants_does_not_contain_sequence("Res")
        filtered_restaurants_does_not_contain_sequence("ant")
        filtered_restaurants_does_not_contain_sequence("an")
        filtered_restaurants_does_not_contain_sequence("1")
        filtered_restaurants_does_not_contain_sequence(" ")
        filtered_restaurants_does_not_contain_sequence("aur")
    }

    private fun filtered_restaurants_does_not_contain_sequence(sequence: String) {
        //GIVEN
        var restaurantsList = arrayListOf(
            Restaurant(name = "Restaurant1"),
            Restaurant(name = "Rest Aurant 2"),
            Restaurant(name = "R"),
            Restaurant(name = "R e s t a u r a n t"),
            Restaurant(name = "123Restaurant"),
            Restaurant(name = "1 Restaurant"),
            Restaurant(name = "1 Restaurant "),
            Restaurant(name = "Rest Aurant 2"),
            Restaurant(name = "Random")
        )
        viewModel.restaurantsListLiveData.value = restaurantsList
        viewModel.filteredRestaurantsListLiveData.value = restaurantsList

        //WHEN
        viewModel.filterRestaurantsByName(sequence)

        //THEN
        (viewModel.filteredRestaurantsListLiveData.value as ArrayList<Restaurant>).forEach {
            assert(it.name.toLowerCase().contains(sequence.toLowerCase()))
        }
    }
}

