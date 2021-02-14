package com.alex.mainmodule.presentation.fragments

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import com.alex.mainmodule.presentation.fragments.adapters.RestaurantsAndReviewsAdapter
import kotlinx.android.synthetic.main.restaurant_details_fragment.*
import kotlinx.android.synthetic.main.restaurants_list_fragment.*
import me.saket.inboxrecyclerview.animation.ItemExpandAnimator
import me.saket.inboxrecyclerview.dimming.DimPainter
import me.saket.inboxrecyclerview.page.InterceptResult
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class RestaurantsListFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.restaurants_list_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner, {
            if (it == MainActivityViewState.ShowRestaurantsList) {
                restaurantsListRecyclerView.collapse()
            }
        })
        setupExpandablePageLayout()
        setupRecyclerView(setupAdapter())
        setupButtons()
    }

    private fun setupButtons() {
        restaurantsFilterSlider.addOnChangeListener { slider, _, _ ->
            viewModel.filterRestaurantsByRating(slider.values[0], slider.values[1])
        }
    }

    private fun setupExpandablePageLayout() {

        restaurantsExpandablePage.pullToCollapseInterceptor = { downX, downY, upwardPull ->
            if (restaurantReviewsRecyclerView.globalVisibleRect().contains(downX, downY).not()) {
                InterceptResult.IGNORED
            } else {
                val directionInt = if (upwardPull) +1 else -1
                val canScrollFurther =
                    restaurantReviewsRecyclerView.canScrollVertically(directionInt)
                when {
                    canScrollFurther -> InterceptResult.INTERCEPTED
                    else -> InterceptResult.IGNORED
                }
            }
        }

        restaurantsExpandablePage.addStateChangeCallbacks(object :
            SimplePageStateChangeCallbacks() {
            override fun onPageCollapsed() {
                restaurantDetailsScrollView.scrollTo(0, 0)
                viewModel.onRestaurantCollapsed()
            }
        })
    }

    private fun setupRecyclerView(adapter: RestaurantsAndReviewsAdapter) {
        restaurantsListRecyclerView.layoutManager = LinearLayoutManager(context)
        restaurantsListRecyclerView.expandablePage = restaurantsExpandablePage
        restaurantsListRecyclerView.dimPainter = DimPainter.listAndPage(
            listColor = Color.WHITE,
            listAlpha = 0.75F,
            pageColor = Color.WHITE,
            pageAlpha = 0.65f
        )
        restaurantsListRecyclerView.itemExpandAnimator = ItemExpandAnimator.split()

        restaurantsListRecyclerView.adapter = adapter
    }

    private fun setupAdapter(): RestaurantsAndReviewsAdapter {
        val restaurantsListAdapter = RestaurantsAndReviewsAdapter()

        viewModel.filteredRestaurantsListLiveData.observe(viewLifecycleOwner) {
            onFilteredRestaurantsListChanged(it, restaurantsListAdapter)
            onReviewsChanged(it, restaurantsListAdapter)

        }

        viewModel.usersListLiveData.observe(viewLifecycleOwner) {
            restaurantsListAdapter.isOwner = viewModel.getUserRole() == Role.OWNER
            restaurantsListAdapter.notifyListsChanged()
        }


        restaurantsListAdapter.onRestaurantItemClick = {
            viewModel.goToViewRestaurant(it.first)
            restaurantsListRecyclerView.expandItem(it.second)
            expandRestaurantFragment()
        }

        restaurantsListAdapter.onReviewPendingReplyItemClick = {
            viewModel.goToAddReply(it)
        }

        restaurantsListRecyclerView.layoutManager = LinearLayoutManager(context)
        restaurantsListRecyclerView.adapter = restaurantsListAdapter

        return restaurantsListAdapter
    }

    private fun onFilteredRestaurantsListChanged(
        filteredRestaurantsList: List<Restaurant>,
        adapter: RestaurantsAndReviewsAdapter
    ) {
        if (filteredRestaurantsList.isNullOrEmpty().not()) {
            numberOfRestaurantsTv.text =
                getString(R.string.number_of_restaurants, filteredRestaurantsList.size)
            adapter.restaurantsList = ArrayList(filteredRestaurantsList)
            adapter.notifyListsChanged()
        }
    }

    private fun onReviewsChanged(
        restaurantsList: List<Restaurant>,
        adapter: RestaurantsAndReviewsAdapter
    ) {
        if (restaurantsList.isNullOrEmpty().not()) {
            restaurantsFilterSlider.setValues(0f, 5f)
            val reviewsPendingReplyList = arrayListOf<Review>()
            restaurantsList.forEach { restaurant ->
                restaurant.reviews.forEach { review ->
                    if (review.reply.isEmpty()) {
                        reviewsPendingReplyList.add(review)
                    }
                }
            }
            adapter.reviewsList = reviewsPendingReplyList
            adapter.notifyListsChanged()
        }
    }

    private fun expandRestaurantFragment() {
        var expandableFragment =
            childFragmentManager.findFragmentById(restaurantsExpandablePage.id) as RestaurantFullDetailsFragment?
        if (expandableFragment == null) {
            expandableFragment = RestaurantFullDetailsFragment()
        }

        childFragmentManager
            .beginTransaction()
            .replace(restaurantsExpandablePage.id, expandableFragment)
            .commitNowAllowingStateLoss()
    }


    private fun View.globalVisibleRect(): RectF {
        val rect = Rect()
        getGlobalVisibleRect(rect)
        return RectF(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat()
        )
    }
}

