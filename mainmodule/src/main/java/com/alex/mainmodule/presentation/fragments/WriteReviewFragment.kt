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
import com.alex.mainmodule.presentation.MainActivityViewModel
import com.alex.mainmodule.presentation.MainActivityViewState
import kotlinx.android.synthetic.main.write_review_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import java.util.*

class WriteReviewFragment : Fragment(), KoinComponent {
    private val viewModel: MainActivityViewModel by sharedViewModel()
    private val calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addOnSendReviewListener()

        return inflater.inflate(
            R.layout.write_review_fragment,
            container,
            false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()

        if (viewModel.viewState.value == MainActivityViewState.ShowEditReviewScreen) {
            showEditMode()
        }
    }

    private fun showEditMode() {
        reviewEditModeTv.visibility = View.VISIBLE
        deleteReviewIv.visibility = View.VISIBLE

        viewModel.selectedReviewLiveData.observe(viewLifecycleOwner) {
            reviewRatingBar.rating = it.restaurantOverallEvaluation
            reviewTitle.setText(it.title)
            reviewDescription.setText(it.description)
            calendar.timeInMillis = it.visitDateInMillis
            visitDateTv.text = DateFormat.getDateInstance(DateFormat.LONG)
                .format(calendar.timeInMillis)
        }

        deleteReviewIv.setOnClickListener {
            viewModel.deleteReview()
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
            datePicker.show()
        }
    }

    private fun addOnSendReviewListener() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                MainActivityViewState.SendReview -> viewModel.addReview(getReview())
                MainActivityViewState.EditReview -> viewModel.editReview(getReview())
                else -> {

                }
            }
        })
    }

    private fun getReview() = Review(
        restaurantOverallEvaluation = reviewRatingBar.rating,
        title = reviewTitle.text.toString(),
        description = reviewDescription.text.toString(),
        visitDateInMillis = calendar.timeInMillis
    )


}