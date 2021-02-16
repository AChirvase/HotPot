package com.alex.mainmodule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.framework.mainModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RepositoryInstrumentedTest : KoinTest {

    private val repository: Repository by inject()

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val exception: ExpectedException = ExpectedException.none()


    @Before
    fun setUp() {
        try {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext)
                modules(mainModule)
            }
        } catch (e: Exception) {
            //N/A, koin started again
        }
    }

    @Test
    fun restaurants_list_live_data_not_empty() {
        runBlocking {
            repository.getRestaurantsLiveData().observeOnce {
                assert(it.isNullOrEmpty().not())
            }
        }
    }


    @Test
    fun restaurant_is_added_and_deleted_correctly() {
        var name = "MyRestaurantForTest"
        var restaurant = Restaurant(name = name)

        repository.getRestaurantsLiveData().observeOnce {
            val addedRestaurant = it.find { restaurant -> restaurant.name == name }
            assertNotNull(addedRestaurant)
            restaurant_is_deleted_correctly(addedRestaurant!!)
        }
        repository.addRestaurant(restaurant)

    }

    private fun restaurant_is_deleted_correctly(restaurantToDelete: Restaurant) {
        var result = repository.deleteRestaurant(restaurantToDelete)
        result.addOnCompleteListener { task ->
            assert(task.isSuccessful)
        }
    }

    @Test
    fun user_is_added_and_deleted_correctly() {
        var email = "user@user.com"
        var user = User(email = email)

        repository.getUsersListLiveData().observeOnce {
            val addedUser = it.find { user -> user.email == email }
            assertNotNull(addedUser)
            user_is_deleted_correctly(addedUser!!)
        }
        repository.addUser(user)

    }

    private fun user_is_deleted_correctly(userToDelete: User) {
        val result = repository.deleteUser(userToDelete)
        result.addOnCompleteListener { task ->
            assert(task.isSuccessful)
        }
    }

    @Test
    fun empty_user_email_add_to_firebase_throws_error() {
        val user = User("user", "", "1234")
        exception.expect(IllegalArgumentException::class.java)
        repository.addUser(user)
    }

    @Test
    fun user_is_correctly_updated_locally() {
        val user = User("user", "user@user.com", "1234")

        repository.updateCurrentUserData(user)

        assertEquals(user, repository.getCurrentUser())

    }

    @Test
    fun restaurants_retrieved_from_REST_API() {
        runBlocking {
            try {
                val response = repository.getRestaurantsList()
                assert(response.body().isNullOrEmpty().not())
            } catch (e: Exception) {
                fail(e.message)
            }
        }
    }

}