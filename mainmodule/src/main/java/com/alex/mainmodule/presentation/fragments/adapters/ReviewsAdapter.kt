package com.alex.mainmodule.presentation.fragments.adapters

import android.content.res.ColorStateList
import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.review_gravity_left.view.*
import java.util.*
import kotlin.collections.ArrayList

class ReviewsAdapter :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
    var reviewsList: ArrayList<Review> = ArrayList()
    var usersList: ArrayList<User> = ArrayList()
    var hasEditRights: Boolean = false

    //this is a callback
    var onItemClick: ((Review) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.review_gravity_left, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = reviewsList[position]
        viewHolder.reviewRatingBar.rating = item.restaurantOverallEvaluation
        viewHolder.reviewTitle.text = item.title
        viewHolder.reviewVisitDate.text = DateFormat.getDateInstance(DateFormat.LONG)
            .format(Date(item.visitDateInMillis))
        viewHolder.reviewDescription.text = item.description

        with(viewHolder.reviewTypeTv) {
            when (position) {
                0 -> {
                    visibility = View.VISIBLE
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                    text = resources.getString(R.string.best_review_type)
                }
                1 -> {
                    visibility = View.VISIBLE
                    backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                    text = resources.getString(R.string.worst_review_type)
                }
                else -> {
                    visibility = View.GONE
                }
            }
        }

        if (item.reply.isNotEmpty()) {
            viewHolder.replyGroup.visibility = View.VISIBLE
            viewHolder.reviewReplyBtn.visibility = View.GONE
            viewHolder.reviewReply.text = item.reply
        } else {
            viewHolder.replyGroup.visibility = View.GONE
            if (hasEditRights) {
                viewHolder.reviewReplyBtn.visibility = View.VISIBLE
            }
        }
        val path = usersList.find { it.email == item.userEmail }?.image
        if (path.isNullOrEmpty().not()) {
            Picasso.get().load(path).centerCrop()
                .fit()
                .into(viewHolder.userImg)
        }
    }


    override fun getItemCount() = reviewsList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var reviewRatingBar: RatingBar = view.reviewRatingBar
        var reviewTitle: TextView = view.reviewTitle
        var reviewVisitDate: TextView = view.reviewVisitDate
        var reviewDescription: TextView = view.reviewDescription
        var reviewReply: TextView = view.reviewReply
        var replyGroup: Group = view.replyGroup
        var reviewReplyBtn: TextView = view.reviewReplyBtn
        var userImg: ImageView = view.userImg
        var reviewTypeTv: TextView = view.reviewTypeTv

        init {
            view.setOnClickListener { onItemClick?.invoke(reviewsList[adapterPosition]) }
        }
    }
}