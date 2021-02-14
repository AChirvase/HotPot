package com.alex.mainmodule.presentation.fragments

import android.app.DatePickerDialog
import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alex.mainmodule.R
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import kotlinx.android.synthetic.main.add_review_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import java.util.*

class AddReviewFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()
    private val calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        subscribeForViewStateChanges()

        return inflater.inflate(
            R.layout.add_review_fragment,
            container,
            false
        )
    }

    private fun subscribeForViewStateChanges() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                MainActivityViewState.AddReview -> viewModel.addReview(getReview())
                MainActivityViewState.EditReview -> viewModel.editReview(getReview())
                MainActivityViewState.ShowEditReviewScreen -> showEditMode()
                else -> {

                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()

        when (viewModel.getUserRole()) {
            Role.ADMIN -> showViewsForAdmin()
            Role.REGULAR -> showViewsForRegular()
            Role.OWNER -> showViewsForOwner()
        }
    }

    private fun showViewsForOwner() {
        reviewGroup.visibility = View.GONE
        replyGroup.visibility = View.VISIBLE
        reviewAuthorEt.visibility = View.GONE
    }

    private fun showViewsForRegular() {
        reviewGroup.visibility = View.VISIBLE
        replyGroup.visibility = View.GONE
        reviewAuthorEt.visibility = View.GONE
    }

    private fun showViewsForAdmin() {
        reviewGroup.visibility = View.VISIBLE
        replyGroup.visibility = View.VISIBLE
        reviewAuthorEt.visibility = View.VISIBLE
    }

    private fun showEditMode() {
        editGroup.visibility = View.VISIBLE

        viewModel.selectedReviewLiveData.observe(viewLifecycleOwner) {
            reviewRatingBar.rating = it.restaurantOverallEvaluation
            reviewTitle.setText(it.title)
            reviewDescription.setText(it.description)
            reviewReplyEt.setText(it.reply)
            calendar.timeInMillis = it.visitDateInMillis
            visitDateTv.text = DateFormat.getDateInstance(DateFormat.LONG)
                .format(calendar.timeInMillis)
            reviewAuthorEt.setText(it.userEmail)

            if (viewModel.getUserRole() == Role.OWNER && it.reply.isEmpty()) {
                editGroup.visibility = View.GONE
            }
        }

        deleteReviewIv.setOnClickListener {
            viewModel.onDeleteBtnPressed()
        }
    }


    private fun setupDatePicker() {
        visitDateTv.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.YEAR, year)

                    visitDateTv.text = DateFormat.getDateInstance(DateFormat.LONG)
                        .format(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }


    private fun getReview() = Review(
        restaurantOverallEvaluation = reviewRatingBar.rating,
        title = reviewTitle.text.toString(),
        description = reviewDescription.text.toString(),
        visitDateInMillis = calendar.timeInMillis,
        reply = reviewReplyEt.text.toString(),
        userEmail = reviewAuthorEt.text.toString().replace(" ", "")
    )


}