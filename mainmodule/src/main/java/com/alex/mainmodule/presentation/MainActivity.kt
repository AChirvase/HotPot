package com.alex.mainmodule.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.alex.mainmodule.R
import com.alex.mainmodule.presentation.fragments.*
import com.alex.mainmodule.utils.Utils.hideSystemUI
import com.alex.mainmodule.utils.Utils.showFragment
import kotlinx.android.synthetic.main.bottom_app_bar.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {
    private val viewModel: MainActivityViewModel by viewModel()

    private var restaurantsListFragment = RestaurantsListFragment()
    private var usersListFragment = UsersListFragment()
    private var addReviewFragment = AddReviewFragment()
    private var addRestaurantFragment = AddRestaurantFragment()
    private var addUserFragment = AddUserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
        subscribeForViewStateChange()
        hideSystemUI(window)
    }

    private fun subscribeForViewStateChange() {
        viewModel.viewState.observe(this, { viewState -> updateViewState(viewState) })
    }

    private fun updateViewState(viewState: MainActivityViewState) {
        supportFragmentManager.executePendingTransactions()
        setupAppBar()
        when (viewState) {
            MainActivityViewState.ShowRestaurantsList,
            MainActivityViewState.ShowRestaurant ->
                showFragment(restaurantsListFragment, supportFragmentManager, true)
            MainActivityViewState.ShowEditReviewScreen,
            MainActivityViewState.ShowAddReviewScreen ->
                showFragment(addReviewFragment, supportFragmentManager, true)
            MainActivityViewState.ShowAddRestaurantScreen,
            MainActivityViewState.ShowEditRestaurantScreen ->
                showFragment(addRestaurantFragment, supportFragmentManager)
            MainActivityViewState.ShowAddUserScreen,
            MainActivityViewState.ShowEditUserScreen ->
                showFragment(addUserFragment, supportFragmentManager)
            MainActivityViewState.ShowUsersList ->
                showFragment(usersListFragment, supportFragmentManager)
            MainActivityViewState.ExitApp -> finishAffinity()
            MainActivityViewState.FinishActivity -> finish()
            else -> {
            }
        }
    }

    private fun setupAppBar() {

        bottomAppBarMainBtn.setImageResource(viewModel.getImageForMainBtn())

        bottomAppBarLeftBtn.setImageResource(viewModel.getImageForLeftBtn())

        viewModel.getImageForFarRightBtn()?.let {
            bottomAppBarFarRightBtn.setImageResource(it)
            bottomAppBarFarRightBtn.visibility = View.VISIBLE

        } ?: run {
            bottomAppBarFarRightBtn.visibility = View.GONE
        }

        viewModel.getImageForMiddleRightBtn()?.let {
            bottomAppBarMiddleRightBtn.setImageResource(it)
            bottomAppBarMiddleRightBtn.visibility = View.VISIBLE

        } ?: run {
            bottomAppBarMiddleRightBtn.visibility = View.GONE
        }
    }


    private fun hideOtherFragments() {

    }

    private fun showAddUserFragment() {
        if (addUserFragment.isAdded) {
            supportFragmentManager.popBackStackImmediate(addUserFragment.javaClass.name, 0)
            return
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.mainActivityFragmentContainer,
                addUserFragment
            ).addToBackStack(addUserFragment.javaClass.name)
            .commit()
    }


    private fun showAddRestaurantFragment() {
        if (addRestaurantFragment.isAdded) {
            supportFragmentManager.popBackStackImmediate(addRestaurantFragment.javaClass.name, 0)
            return
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.mainActivityFragmentContainer,
                addRestaurantFragment
            ).addToBackStack(addRestaurantFragment.javaClass.name)
            .commit()
    }

    private fun showWriteReviewFragment() {
        if (addReviewFragment.isAdded) {
            supportFragmentManager.popBackStackImmediate(addReviewFragment.javaClass.name, 0)
            return
        }
        supportFragmentManager.beginTransaction()
            .hide(restaurantsListFragment)
            .add(
                R.id.mainActivityFragmentContainer,
                addReviewFragment
            ).addToBackStack(addReviewFragment.javaClass.name)
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
        bottomAppBarLeftBtn.setOnClickListener {
            viewModel.onBottomAppBarLeftBtnClicked()
        }
        bottomAppBarMiddleRightBtn.setOnClickListener {
            viewModel.onBottomAppBarMiddleRightBtnClicked()
        }
        bottomAppBarFarRightBtn.setOnClickListener {
            viewModel.onBottomAppBarFarRightBtn2Clicked()
        }
    }
}