package com.alex.mainmodule.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.alex.mainmodule.R
import com.alex.mainmodule.presentation.fragments.RestaurantsListFragment
import com.alex.mainmodule.presentation.fragments.UsersListFragment
import com.alex.mainmodule.presentation.fragments.WriteReviewFragment
import kotlinx.android.synthetic.main.bottom_app_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {
    private val viewModel: MainActivityViewModel by viewModel()

    private var restaurantsListFragment = RestaurantsListFragment()
    private var usersListFragment = UsersListFragment()
    private var writeReviewFragment = WriteReviewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
        subscribeForViewStateChange()
    }

    private fun subscribeForViewStateChange() {
        viewModel.viewState.observe(this, { viewState -> updateViewState(viewState) })
    }

    private fun updateViewState(viewState: MainActivityViewState) {
        supportFragmentManager.executePendingTransactions()
        when (viewState) {
            MainActivityViewState.ViewRestaurantsList -> {
                setupAppBarForRestaurantsList()
                showRestaurantsListFragment()
            }
            MainActivityViewState.ViewRestaurant -> {
                setupAppBarForViewRestaurant()
                showRestaurantsListFragment()
            }
            MainActivityViewState.ShowEditReviewScreen -> {
                setupAppBarForWriteReview()
                showWriteReviewFragment()
            }
            MainActivityViewState.ShowWriteReviewScreen -> {
                setupAppBarForWriteReview()
                showWriteReviewFragment()
            }
            MainActivityViewState.ViewUsersList -> showUsersListFragment()
            MainActivityViewState.ExitApp -> finishAffinity()
            MainActivityViewState.FinishActivity -> finish()
            else -> {
            }
        }
    }

    private fun setupAppBarForWriteReview() {
        bottomAppBarMainBtn.setImageResource(R.drawable.ic_baseline_send_24)
    }

    private fun setupAppBarForRestaurantsList() {
        bottomAppBarMainBtn.setImageResource(R.drawable.ic_baseline_search_24)
    }

    private fun setupAppBarForViewRestaurant() {
        bottomAppBarMainBtn.setImageResource(R.drawable.ic_baseline_rate_review_24)
    }


    private fun showWriteReviewFragment() {
        if (writeReviewFragment.isAdded) {
            supportFragmentManager.popBackStackImmediate(writeReviewFragment.javaClass.name, 0)
            return
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.mainActivityFragmentContainer,
                writeReviewFragment
            ).addToBackStack(null)
            .commit()
    }

    private fun showRestaurantsListFragment() {
        if (restaurantsListFragment.isAdded) {
            supportFragmentManager.popBackStack(restaurantsListFragment.javaClass.name, 0)
            return
        }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.mainActivityFragmentContainer,
                restaurantsListFragment
            ).addToBackStack(restaurantsListFragment.javaClass.name)
            .commit()
    }

    private fun showUsersListFragment() {
        if (usersListFragment.isAdded) {
            supportFragmentManager.popBackStackImmediate(
                usersListFragment.javaClass.name,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            return
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.mainActivityFragmentContainer,
                usersListFragment
            )
            .addToBackStack(usersListFragment.javaClass.name)
            .commit()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        setupButtons()

    }

    private fun setupButtons() {
        bottomAppBarMainBtn.setOnClickListener {
            viewModel.onMainButtonClicked()
        }
        bottomAppBarBtn1.setOnClickListener {
            viewModel.signOut()
        }
        bottomAppBarBtn3.setOnClickListener {
            viewModel.addRestaurantsTest()
        }
        bottomAppBarBtn4.setOnClickListener {
            viewModel.onSwitchUsersRestaurantsPressed()
        }
    }
}