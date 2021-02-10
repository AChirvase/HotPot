package com.alex.mainmodule.presentation.fragments

import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.presentation.MainActivityViewModel
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
        restaurantReviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        restaurantReviewsRecyclerView.adapter = reviewsAdapter

        reviewsAdapter.onItemClick = {
            viewModel.showEditReviewScreen(it)
        }

        viewModel.selectedRestaurantLiveData.observe(viewLifecycleOwner) { restaurant ->
            if (restaurant == null) {
                return@observe
            }
            updateRestaurantInfo(restaurant, reviewsAdapter)
        }


        val imageUri = "https://i.imgur.com/n6bF2Vx.jpeg"
        Picasso.get().load(imageUri).centerCrop().fit().into(restaurantTopContainerBackground)

    }

    private fun updateRestaurantInfo(restaurant: Restaurant, reviewsAdapter: ReviewsAdapter) {
        restaurantName.text = restaurant.name
        restaurantAddress.text = restaurant.address
        restaurantNumberOfReviews.text = restaurant.reviews.size.toString()
        restaurantRatingBar.rating =
            restaurant.reviews.map { it.restaurantOverallEvaluation }.average().toFloat()

        reviewsAdapter.reviewsList = restaurant.reviews
        reviewsAdapter.reviewsList.sortBy { it.dateInMillis }
        reviewsAdapter.notifyDataSetChanged()
    }


    private class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
        var reviewsList: ArrayList<Review> = ArrayList()

        //this is a callback
        var onItemClick: ((Review) -> Unit)? = null

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.review_gravity_left, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.reviewRatingBar.rating = reviewsList[position].restaurantOverallEvaluation
            viewHolder.reviewTitle.text = reviewsList[position].title
            viewHolder.reviewVisitDate.text = DateFormat.getDateInstance(DateFormat.LONG)
                .format(Date(reviewsList[position].visitDateInMillis))
            viewHolder.reviewDescription.text = reviewsList[position].description

        }

        override fun getItemCount() = reviewsList.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var reviewRatingBar: RatingBar = view.reviewRatingBar
            var reviewTitle: TextView = view.reviewTitle
            var reviewVisitDate: TextView = view.reviewVisitDate
            var reviewDescription: TextView = view.reviewDescription

            init {
                view.setOnClickListener { onItemClick?.invoke(reviewsList[adapterPosition]) }
            }
        }
    }

}