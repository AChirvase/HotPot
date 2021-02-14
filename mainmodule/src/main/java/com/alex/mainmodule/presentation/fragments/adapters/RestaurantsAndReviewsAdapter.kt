package com.alex.mainmodule.presentation.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.restaurants_list_item.view.*
import kotlinx.android.synthetic.main.review_pending_reply_item.view.*

class RestaurantsAndReviewsAdapter
    : RecyclerView.Adapter<RestaurantsAndReviewsAdapter.BaseViewHolder<*>>() {

    companion object {
        const val TYPE_RESTAURANT = 1
        const val TYPE_REVIEW = 2
    }

    private var adapterList = ArrayList<Any>()
    var restaurantsList = ArrayList<Restaurant>()
    var reviewsList = ArrayList<Review>()
    var isOwner = false

    //this is a callback
    var onRestaurantItemClick: ((Pair<Restaurant, Long>) -> Unit)? = null
    var onReviewPendingReplyItemClick: ((Review) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    fun notifyListsChanged() {
        adapterList.clear()
        if (isOwner) {
            adapterList.addAll(reviewsList)
        }
        adapterList.addAll(restaurantsList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_RESTAURANT -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.restaurants_list_item, viewGroup, false)
                RestaurantViewHolder(view)
            }
            TYPE_REVIEW -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.review_pending_reply_item, viewGroup, false)
                ReviewViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = adapterList[position]
        when (holder) {
            is RestaurantViewHolder -> holder.bind(item as Restaurant)
            is ReviewViewHolder -> holder.bind(item as Review)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (adapterList[position]) {
            is Restaurant -> TYPE_RESTAURANT
            is Review -> TYPE_REVIEW
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun getItemCount() = adapterList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    inner class RestaurantViewHolder(view: View) : BaseViewHolder<Restaurant>(view) {
        var restaurantName: TextView = view.restaurantNameTv
        var restaurantPicture: ImageView = view.restaurantPictureIv
        var restaurantScheduleTv: TextView = view.restaurantScheduleTv
        var restaurantAddressTv: TextView = view.restaurantAddressTv
        var restaurantRatingBar: RatingBar = view.restaurantRatingBar

        init {
            view.setOnClickListener {
                onRestaurantItemClick?.invoke(
                    Pair(
                        adapterList[adapterPosition] as Restaurant,
                        getItemId(adapterPosition)
                    )
                )
            }
        }

        override fun bind(item: Restaurant) {
            restaurantName.text = item.name
            restaurantAddressTv.text = item.address
            restaurantRatingBar.rating = item.rating
            if (item.picture.isEmpty().not()) {
                Picasso.get().load(item.picture).centerCrop().fit().into(restaurantPicture)
            }

        }
    }

    inner class ReviewViewHolder(view: View) : BaseViewHolder<Review>(view) {
        var userEmailTv: TextView = view.userEmailTv
        var reviewTitleTv: TextView = view.reviewTitleTv
        var reviewDescriptionTv: TextView = view.reviewDescriptionTv
        var restaurantNameTv: TextView = view.reviewRestaurantNameTv
        var reviewRatingBar: RatingBar = view.reviewRatingBar

        init {
            view.setOnClickListener {
                onReviewPendingReplyItemClick?.invoke(adapterList[adapterPosition] as Review)
            }
        }

        override fun bind(item: Review) {
            userEmailTv.text = item.userEmail
            reviewTitleTv.text = item.title
            reviewDescriptionTv.text = item.description
            reviewRatingBar.rating = item.restaurantOverallEvaluation
            restaurantNameTv.text = restaurantsList.find { it.reviews.contains(item) }?.name
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

}