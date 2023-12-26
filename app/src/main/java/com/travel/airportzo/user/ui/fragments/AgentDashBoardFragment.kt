package com.travel.airportzo.user.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import com.travel.airportzo.user.databinding.AgentDashboardFragmentBinding
import com.travel.airportzo.user.model.AgentDashboardData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.AgentDashboardAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Agent dash board fragment
 *
 * @constructor Create empty Agent dash board fragment
 */
class AgentDashBoardFragment : BaseFragment() {

    private val agentDashBoardFragment by lazy { AgentDashboardFragmentBinding.inflate(layoutInflater) }

    private val agentBookingDetail:ArrayList<AgentDashboardData> by lazy{ arrayListOf()}

    private val agentBooking by lazy { context?.let { AgentDashboardAdapter(it,agentBookingDetail) }}

     private var date:String=""
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         val calendar: Calendar = Calendar.getInstance()
         val simpleDateFormat = SimpleDateFormat("MMM yyyy")
         val date = simpleDateFormat.format(calendar.time)

         agentDashBoardFragment.dateText.text=date
         monthlyFilter(date)
        agentDashBoardFragment.agentDashboardBack.setOnDebounceListener {
            onBackPressed()
        }

        agentDashBoardFragment.mrImg.setOnDebounceListener {
            agentBookingDetail.clear()
            agentDashBoardFragment.citySpinner.performClick()
            yearPicker()
        }
    }


    private fun yearPicker() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM")
                    val formatter = SimpleDateFormat("MMM yyyy")
                    val output = formatter.format(parser.parse(dateType))
                   date= agentDashBoardFragment.dateText.setText(output).toString()
                    date= agentDashBoardFragment.dateText.text.toString()
                    monthlyFilter(output)
                }, year, month, day

            )
//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show()
        }



    private fun monthlyFilter(date: String) {
        agentBookingDetail.clear()
        agentDashBoardFragment.agentMileRecycler.adapter?.notifyDataSetChanged()
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("month_filter",date)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.agentdashboard(jsonObject = jsonObject)?.observe(requireActivity(),agent)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val agent=Observer<ApiResult<List<AgentDashboardData>>>{
        when(it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                agentBookingDetail.clear()
                agentBookingDetail.addAll(it.data)
                agentDashBoardFragment.agentMileRecycler.adapter=agentBooking
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
        }
    }



    private fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return agentDashBoardFragment.root
    }
}