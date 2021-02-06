package com.alex.mainmodule.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alex.mainmodule.R
import com.alex.mainmodule.presentation.fragments.RestaurantsListFragment
import kotlinx.android.synthetic.main.main_activity_layout.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {
    private val viewModel: MainActivityViewModel by viewModel()

    var restaurantsListFragment = RestaurantsListFragment()

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
            MainActivityViewState.ViewRestaurantsList -> showRestaurantsListFragment()
            MainActivityViewState.ExitApp -> finish()
            else -> {
            }
        }
    }

    private fun showRestaurantsListFragment() {
        if (restaurantsListFragment.isAdded) {
            supportFragmentManager.popBackStack(restaurantsListFragment.javaClass.name, 0)
            return
        }
        supportFragmentManager.beginTransaction()
            .add(
                R.id.mainActivityFragmentContainer,
                restaurantsListFragment
            )
            .addToBackStack(restaurantsListFragment.javaClass.name)
            .commit()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        mainActivityFab.setOnClickListener {
            viewModel.addRestaurantsTest()
        }
    }
}