package com.travel.airportzo.user.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.AgentMyselfFragmentBinding
import com.travel.airportzo.user.model.MyBookingData
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.adapter.AgentMySelfAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import kotlinx.coroutines.launch


class AgentMySelfFragment : BaseFragment() {

    private val myselfAgent by lazy { AgentMyselfFragmentBinding.inflate(layoutInflater) }
    private val agentMyselfData: ArrayList<MyBookingData> by lazy { arrayListOf() }
    private val agentMyselfAdapter by lazy { AgentMySelfAdapter(agentMyselfData,::clicked) }

    private var agentMyself : Boolean = false

    private fun clicked(token: String) {
        findNavController().navigate(R.id.action_agentBookingFragment_to_bookingDetailsFragment, bundleOf("token" to token))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingTicketList()
    }

    private fun bookingTicketList() {
        val token = SavedSharedPreference.getUserData(requireContext()).token.toString()
        val jsonObject = JsonObject().apply {
            addProperty("user_token",token)
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

    private val bookingData= Observer<ApiResult<List<MyBookingData>>>{
        when(it){
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                agentMyselfData.clear()
                for (list in 0 until it.data.size) {
                    agentMyself=it.data[list].for_others
                   if (!agentMyself) {
                       agentMyselfData.add(it.data[list])
                   }
                }
                myselfAgent.bookingList.adapter = agentMyselfAdapter
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return myselfAgent.root
    }
}