package com.alex.mainmodule.presentation.fragments

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import kotlinx.android.synthetic.main.restaurant_details_fragment.*
import kotlinx.android.synthetic.main.restaurants_list_fragment.*
import kotlinx.android.synthetic.main.restaurants_list_item.view.*
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
        filterRestaurantsTv.setOnClickListener {

        }
    }

    private fun setupExpandablePageLayout() {

        restaurantsExpandablePage.pullToCollapseInterceptor = { downX, downY, upwardPull ->
            if (restaurantDetailsScrollView.globalVisibleRect().contains(downX, downY).not()) {
                InterceptResult.IGNORED
            } else {
                val directionInt = if (upwardPull) +1 else -1
                val canScrollFurther = restaurantDetailsScrollView.canScrollVertically(directionInt)
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

    private fun setupRecyclerView(adapter: RestaurantsListAdapter) {
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

    private fun setupAdapter(): RestaurantsListAdapter {
        val restaurantsListAdapter = RestaurantsListAdapter()

        viewModel.restaurantsListLiveData.observe(viewLifecycleOwner, {
            numberOfRestaurantsTv.text = getString(R.string.number_of_restaurants, it.size)
            restaurantsListAdapter.restaurantsList = it as ArrayList<Restaurant>
            restaurantsListAdapter.notifyDataSetChanged()
        })

        restaurantsListAdapter.onItemClick = {
            viewModel.goToViewRestaurant(it.first)
            restaurantsListRecyclerView.expandItem(it.second)
            expandRestaurantFragment()
        }
        restaurantsListRecyclerView.layoutManager = LinearLayoutManager(context)
        restaurantsListRecyclerView.adapter = restaurantsListAdapter

        return restaurantsListAdapter
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


    class RestaurantsListAdapter
        : RecyclerView.Adapter<RestaurantsListAdapter.ViewHolder>() {

        var restaurantsList = ArrayList<Restaurant>()

        //this is a callback
        var onItemClick: ((Pair<Restaurant, Long>) -> Unit)? = null

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.restaurants_list_item, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.restaurantName.text = restaurantsList[position].name
            viewHolder.restaurantAddressTv.text = restaurantsList[position].address
            viewHolder.restaurantRatingBar.rating =
                restaurantsList[position].reviews.map { it.restaurantOverallEvaluation }.average()
                    .toFloat()
        }

        override fun getItemCount() = restaurantsList.size

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var restaurantName: TextView = view.restaurantNameTv
            var restaurantPicture: ImageView = view.restaurantPictureIv
            var restaurantScheduleTv: TextView = view.restaurantScheduleTv
            var restaurantAddressTv: TextView = view.restaurantAddressTv
            var restaurantRatingBar: RatingBar = view.restaurantRatingBar

            init {
                view.setOnClickListener {
                    onItemClick?.invoke(
                        Pair(
                            restaurantsList[adapterPosition],
                            getItemId(adapterPosition)
                        )
                    )
                }
            }
        }
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

