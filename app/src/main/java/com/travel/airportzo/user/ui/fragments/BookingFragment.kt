package com.travel.airportzo.user.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.model.MyBookingData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.adapter.BookingTicketAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.Utility
import kotlinx.coroutines.launch

class BookingFragment : BaseFragment() {

    private val bookingFragment by lazy {
        com.travel.airportzo.user.databinding.BookingFragmentBinding.inflate(
            layoutInflater
        )
    }

    private val bookTicketData: ArrayList<MyBookingData> by lazy { arrayListOf() }

    private val bookingTicketAdapter by lazy { BookingTicketAdapter(bookTicketData, ::onClicked) }

    private fun onClicked(token: String) {
        findNavController().navigate(R.id.action_cart_to_bookingdetails, bundleOf("token" to token))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /** brand color*/
        bookingFragment.textView6.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))




        return bookingFragment.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookingTicketList()
        Utility.stationarray = JsonArray()




    }




    private fun bookingTicketList() {
        val token = SavedSharedPreference.getUserData(requireContext()).token.toString()
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.bookingHistory(jsonObject = jsonObject)
                    ?.observe(requireActivity(), bookingData)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }


    private val bookingData = Observer<ApiResult<List<MyBookingData>>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                bookTicketData.clear()
                for (list in 0 until it.data.size) {
                    bookTicketData.add(it.data[list])
                    if (bookTicketData.isEmpty()) {
                        bookingFragment.bookingText.visibility = View.VISIBLE
                    } else {
                        bookingFragment.bookingText.visibility = View.GONE
                        bookingFragment.bookingList.adapter = bookingTicketAdapter
                    }
                }
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
        }
    }
}


