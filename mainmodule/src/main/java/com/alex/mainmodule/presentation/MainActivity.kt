package com.alex.mainmodule.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.alex.mainmodule.R
import com.alex.mainmodule.presentation.fragments.*
import com.alex.mainmodule.utils.Utils.hideSystemUI
import com.alex.mainmodule.utils.Utils.showFragment
import kotlinx.android.synthetic.main.bottom_app_bar.*
import kotlinx.android.synthetic.main.top_app_bar.*
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
        setupAppBars()
        when (viewState) {
            MainActivityViewState.ShowRestaurantsList -> {
                showFragment(restaurantsListFragment, supportFragmentManager, true)
                showSearchBar(focus = false, clear = false)
            }
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
            MainActivityViewState.ShowUsersList -> {
                showFragment(usersListFragment, supportFragmentManager)
                showSearchBar(focus = false, clear = true)
            }
            MainActivityViewState.FocusOnSearchRestaurant ->
                showSearchBar(focus = true, clear = true)
            MainActivityViewState.FocusOnSearchUser ->
                showSearchBar(focus = true, clear = true)
            MainActivityViewState.ExitApp -> finishAffinity()
            MainActivityViewState.FinishActivity -> finish()
            else -> {
            }
        }
    }

    private fun setupAppBars() {
        setupBottomBar()
        setupTopBar()
    }

    private fun setupTopBar() {
        searchBar.addTextChangedListener {
            viewModel.onSearchBarTextChanged(it.toString())
        }
        hideSearchBar()
    }

    private fun setupBottomBar() {
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

    private fun showSearchBar(focus: Boolean, clear: Boolean) {
        searchBar.visibility = View.VISIBLE
        if (focus) {
            searchBar.requestFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
        }
        if (clear) {
            searchBar.text.clear()
        }

    }

    private fun hideSearchBar() {
        searchBar.visibility = View.GONE
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
            viewModel.onBottomAppBarFarRightBtnClicked()
        }
    }
}