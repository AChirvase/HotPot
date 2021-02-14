package com.alex.mainmodule.presentation.fragments

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
import com.alex.mainmodule.domain.User
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.fragments.adapters.ReviewsAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.restaurant_details_fragment.*
import kotlinx.android.synthetic.main.review_gravity_left.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import java.util.*
import kotlin.collections.ArrayList


class RestaurantFullDetailsFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.restaurant_details_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRestaurantData()
    }

    private fun setupRestaurantData() {
        val reviewsAdapter = ReviewsAdapter()

        viewModel.usersListLiveData.observe(viewLifecycleOwner) {
            reviewsAdapter.hasEditRights = viewModel.getUserRole() == Role.OWNER
            reviewsAdapter.usersList = it as ArrayList<User>
            reviewsAdapter.notifyDataSetChanged()
        }

        restaurantReviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        restaurantReviewsRecyclerView.adapter = reviewsAdapter

        reviewsAdapter.onItemClick = {
            viewModel.onReviewClicked(it)
        }

        viewModel.selectedRestaurantLiveData.observe(viewLifecycleOwner) { restaurant ->
            if (restaurant == null) {
                return@observe
            }
            updateRestaurantInfo(restaurant, reviewsAdapter)
        }
    }

    private fun updateRestaurantInfo(restaurant: Restaurant, reviewsAdapter: ReviewsAdapter) {
        restaurantName.text = restaurant.name
        restaurantAddress.text = restaurant.address
        restaurantNumberOfReviews.text = restaurant.reviews.size.toString()
        restaurantRatingBar.rating = restaurant.rating

        if (restaurant.picture.isEmpty().not()) {
            Picasso.get().load(restaurant.picture).centerCrop().fit()
                .into(restaurantTopContainerBackground)
        }

        reviewsAdapter.reviewsList = arrangeReviews(restaurant.reviews)
        reviewsAdapter.notifyDataSetChanged()
    }

    private fun arrangeReviews(reviews: ArrayList<Review>): ArrayList<Review> {
        val result = ArrayList<Review>()
        result.addAll(reviews.sortedByDescending { it.dateInMillis })

        val highestRatedReview = result.maxByOrNull { it.restaurantOverallEvaluation }
        val lowestRatedReview = result.minByOrNull { it.restaurantOverallEvaluation }

        lowestRatedReview?.let { result.add(0, it) }
        highestRatedReview?.let { result.add(0, it) }

        return result
    }

}