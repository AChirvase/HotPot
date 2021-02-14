package com.alex.mainmodule.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import kotlinx.android.synthetic.main.add_restaurant_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import java.util.*

class AddRestaurantFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addOnAddRestaurantListener()

        return inflater.inflate(
            R.layout.add_restaurant_fragment,
            container,
            false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.viewState.value == MainActivityViewState.ShowEditRestaurantScreen) {
            showEditMode()
        } else if (viewModel.viewState.value == MainActivityViewState.ShowAddRestaurantScreen) {
            lockOwnerEmail()
        }
    }

    private fun lockOwnerEmail() {
        restaurantOwnerEmailAddressTv.isEnabled = viewModel.getUserRole() == Role.ADMIN
        restaurantOwnerEmailAddressTv.setText(viewModel.getCurrentUser().email)
    }

    private fun showEditMode() {
        restaurantEditModeTv.visibility = View.VISIBLE
        deleteRestaurantIv.visibility = View.VISIBLE


        restaurantOwnerEmailAddressTv.isEnabled = viewModel.getUserRole() == Role.ADMIN

        viewModel.selectedRestaurantLiveData.observe(viewLifecycleOwner) {
            restaurantNameTv.setText(it.name)
            restaurantAddressTv.setText(it.address)
            restaurantOwnerEmailAddressTv.setText(it.ownerEmail)
            restaurantPhoneNumberTv.setText(it.phoneNumber)
        }

        deleteRestaurantIv.setOnClickListener {
            viewModel.deleteRestaurant()
        }
    }

    private fun addOnAddRestaurantListener() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                MainActivityViewState.AddRestaurant -> viewModel.addRestaurant(getRestaurant())
                MainActivityViewState.EditRestaurant -> viewModel.editRestaurant(getRestaurant())
                else -> {

                }
            }
        })
    }

    private fun getRestaurant() = Restaurant(
        name = restaurantNameTv.text.toString(),
        address = restaurantAddressTv.text.toString(),
        ownerEmail = restaurantOwnerEmailAddressTv.text.toString().toLowerCase(Locale.ROOT)
            .replace(" ", ""),
        phoneNumber = restaurantPhoneNumberTv.text.toString()
    )
}